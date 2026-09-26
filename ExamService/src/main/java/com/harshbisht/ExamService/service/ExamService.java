package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.ExamDTO.*;
import com.harshbisht.ExamService.dto.OptionDTO.OptionEditRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionEditRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.OptionEntity;
import com.harshbisht.ExamService.entity.QuestionEntity;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.exception.AccessDeniedException;
import com.harshbisht.ExamService.exception.InvalidRequestException;
import com.harshbisht.ExamService.exception.ResourceNotFoundException;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new AccessDeniedException("Cannot resolve user identity from token");
    }

    private void assertOwnership(ExamEntity exam) {
        if (!exam.getTeacherId().equals(getCurrentUserId())) {
            throw new AccessDeniedException("You are not allowed to modify this exam");
        }
    }

    public ExamResponse createExam(CreateExamRequest request) {
        Long teacherId = getCurrentUserId();

        SubjectEntity subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        ExamEntity exam = new ExamEntity();
        exam.setTitle(request.getTitle());
        exam.setSubject(subject);
        exam.setTeacherId(teacherId);
        exam.setPublished(false);

        return toResponse(examRepository.save(exam));
    }

    public void deleteExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        assertOwnership(exam);

        if (exam.isPublished()) {
            throw new InvalidRequestException("Cannot delete a published exam. Unpublish it first.");
        }

        examRepository.delete(exam);
    }

    public ExamResponse publishExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        assertOwnership(exam);

        exam.setPublished(true);
        return toResponse(examRepository.save(exam));
    }

    public ExamResponse unPublishExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        assertOwnership(exam);

        exam.setPublished(false);
        return toResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse editExam(Long examId, EditExamRequest request) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        assertOwnership(exam);

        SubjectEntity subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        exam.setTitle(request.getTitle());
        exam.setSubject(subject);
        if (request.getPublished() != null) {
            exam.setPublished(request.getPublished());
        }

        if (request.getQuestions() != null) {
            syncQuestions(exam, request.getQuestions());
        }

        return toResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse duplicateExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        assertOwnership(exam);

        ExamEntity copy = new ExamEntity();
        copy.setTitle(exam.getTitle() + " (Copy)");
        copy.setPublished(false);
        copy.setSubject(exam.getSubject());
        copy.setTeacherId(exam.getTeacherId());

        List<QuestionEntity> newQuestions = exam.getQuestions().stream()
                .map(q -> {
                    QuestionEntity newQ = new QuestionEntity();
                    newQ.setQuestionText(q.getQuestionText());
                    newQ.setExam(copy);

                    List<OptionEntity> newOptions = q.getOptions().stream()
                            .map(o -> {
                                OptionEntity newO = new OptionEntity();
                                newO.setText(o.getText());
                                newO.setCorrect(o.isCorrect());
                                newO.setQuestion(newQ);
                                return newO;
                            })
                            .toList();

                    newQ.setOptions(newOptions);
                    return newQ;
                })
                .toList();

        copy.setQuestions(newQuestions);
        return toResponse(examRepository.save(copy));
    }

    public List<ExamResponse> getMyExams() {
        return examRepository.findByTeacherId(getCurrentUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ExamResponse> getExams(Long subjectId, Boolean published) {
        List<ExamEntity> exams;

        if (subjectId != null && published != null) {
            exams = examRepository.findBySubject_IdAndPublished(subjectId, published);
        } else if (subjectId != null) {
            exams = examRepository.findBySubject_Id(subjectId);
        } else if (published != null) {
            exams = examRepository.findByPublished(published);
        } else {
            exams = examRepository.findAll();
        }

        return exams.stream().map(this::toResponse).toList();
    }

    public ExamResponse getExamById(Long examId) {
        return toResponse(
                examRepository.findById(examId)
                        .orElseThrow(() -> new ResourceNotFoundException("Exam not found"))
        );
    }

    public ExamAttemptResponse getExamForAttempt(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.isPublished()) {
            throw new AccessDeniedException("Exam is not published yet");
        }

        List<QuestionAttemptResponse> questionResponses = exam.getQuestions().stream()
                .map(question -> QuestionAttemptResponse.builder()
                        .id(question.getId())
                        .questionText(question.getQuestionText())
                        .options(
                                question.getOptions().stream()
                                        .map(option -> OptionAttemptResponse.builder()
                                                .id(option.getId())
                                                .text(option.getText())
                                                // NOTE: isCorrect is intentionally omitted here
                                                // — OptionAttemptResponse does not have that field.
                                                .build())
                                        .toList()
                        )
                        .build()
                )
                .toList();

        return ExamAttemptResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .questions(questionResponses)
                .build();
    }

    private ExamResponse toResponse(ExamEntity entity) {
        return new ExamResponse(entity.getId(), entity.getTitle(), entity.isPublished(), entity.getTeacherId());
    }

    private void syncQuestions(ExamEntity exam, List<QuestionEditRequest> questionRequests) {
        Map<Long, QuestionEntity> existingMap = exam.getQuestions().stream()
                .collect(Collectors.toMap(QuestionEntity::getId, q -> q));

        List<QuestionEntity> updated = new ArrayList<>();

        for (QuestionEditRequest req : questionRequests) {
            QuestionEntity question;
            if (req.getId() != null && existingMap.containsKey(req.getId())) {
                question = existingMap.get(req.getId());
                question.setQuestionText(req.getQuestionText());
            } else {
                question = new QuestionEntity();
                question.setExam(exam);
                question.setQuestionText(req.getQuestionText());
            }
            syncOptions(question, req.getOptions());
            updated.add(question);
        }

        exam.getQuestions().clear();
        exam.getQuestions().addAll(updated);
    }

    private void syncOptions(QuestionEntity question, List<OptionEditRequest> optionRequests) {
        Map<Long, OptionEntity> existingMap = question.getOptions().stream()
                .collect(Collectors.toMap(OptionEntity::getId, o -> o));

        List<OptionEntity> updated = new ArrayList<>();

        for (OptionEditRequest req : optionRequests) {
            OptionEntity option;
            if (req.getId() != null && existingMap.containsKey(req.getId())) {
                option = existingMap.get(req.getId());
                option.setText(req.getText());
                option.setCorrect(req.isCorrect());
            } else {
                option = new OptionEntity();
                option.setQuestion(question);
                option.setText(req.getText());
                option.setCorrect(req.isCorrect());
            }
            updated.add(option);
        }

        question.getOptions().clear();
        question.getOptions().addAll(updated);
    }

    public com.harshbisht.ExamService.dto.ExamDTO.ExamDetailResponse getExamWithQuestions(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        assertOwnership(exam);

        List<QuestionResponse> questionResponses = exam.getQuestions().stream()
                .map(question -> com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse.builder()
                        .id(question.getId())
                        .questionText(question.getQuestionText())
                        .options(
                                question.getOptions().stream()
                                        .map(option -> com.harshbisht.ExamService.dto.OptionDTO.OptionRequest.builder()
                                                .id(option.getId())
                                                .text(option.getText())
                                                .correct(option.isCorrect())
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
                )
                .toList();

        return com.harshbisht.ExamService.dto.ExamDTO.ExamDetailResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .published(exam.isPublished())
                .subjectId(exam.getSubject().getId())
                .questions(questionResponses)
                .build();
    }

    public com.harshbisht.ExamService.dto.internal.ExamEvaluationResponse getEvaluationData(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        var questions = exam.getQuestions().stream().map(q -> {
            Long correctOptionId = q.getOptions().stream()
                    .filter(OptionEntity::isCorrect)
                    .map(OptionEntity::getId)
                    .findFirst()
                    .orElseThrow(() -> new InvalidRequestException("Question " + q.getId() + " has no correct option"));
            return com.harshbisht.ExamService.dto.internal.ExamEvaluationResponse.QuestionEvaluation.builder()
                    .questionId(q.getId()).marks(1L).correctOptionId(correctOptionId).build();
        }).toList();
        return com.harshbisht.ExamService.dto.internal.ExamEvaluationResponse.builder()
                .examId(exam.getId()).teacherId(exam.getTeacherId()).title(exam.getTitle()).published(exam.isPublished())
                .totalMarks(questions.size()).passingPercentage(40.0).questions(questions).build();
    }
}