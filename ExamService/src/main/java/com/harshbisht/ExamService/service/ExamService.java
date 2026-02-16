package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.ExamDTO.*;
import com.harshbisht.ExamService.dto.OptionDTO.OptionEditRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionEditRequest;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.OptionEntity;
import com.harshbisht.ExamService.entity.QuestionEntity;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    // Helper: Get Current Teacher
    private Long getCurrentTeacherId() {
        return Long.parseLong(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString()
        );
    }

    // Create exam → return DTO
    public ExamResponse createExam(CreateExamRequest request) {

        Long teacherId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        SubjectEntity subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        ExamEntity exam = new ExamEntity();
        exam.setTitle(request.getTitle());
        exam.setSubject(subject);
        exam.setTeacherId(teacherId);
        exam.setPublished(false);

        examRepository.save(exam);

        return toResponse(exam);
    }

    // Delete exam
    public void deleteExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Long currentTeacherId = getCurrentTeacherId();

        if (!exam.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("You are not allowed to modify this exam");
        }

        if (exam.isPublished()) {
            throw new RuntimeException("Cannot delete a published exam");
        }
        examRepository.delete(exam);
    }

    // Publish exam → return DTO
    public ExamResponse publishExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Long currentTeacherId = getCurrentTeacherId();

        if (!exam.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("You are not allowed to modify this exam");
        }

        exam.setPublished(true);
        ExamEntity saved = examRepository.save(exam);
        return toResponse(saved);
    }

    // un publish exam
    public ExamResponse unPublishExam(Long examId) {

        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam doesn't exist"));

        Long currentTeacherId = getCurrentTeacherId();

        if (!exam.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("You are not allowed to modify this exam");
        }

        exam.setPublished(false);
        examRepository.save(exam);

        return toResponse(exam);
    }

    // Edit exam fully → return DTO
    @Transactional
    public ExamResponse editExam(Long examId, EditExamRequest request) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Long currentTeacherId = getCurrentTeacherId();

        if (!exam.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("You are not allowed to modify this exam");
        }

        if (exam.isPublished()) {
            throw new RuntimeException("Cannot edit published exam");
        }

        SubjectEntity subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        exam.setTitle(request.getTitle());
        exam.setSubject(subject);

        syncQuestions(exam, request.getQuestions());

        ExamEntity saved = examRepository.save(exam);
        return toResponse(saved);
    }

    // Mapper: Entity → DTO
    private ExamResponse toResponse(ExamEntity entity) {
        return new ExamResponse(entity.getId(), entity.getTitle(), entity.isPublished());
    }

    // Sync questions
    private void syncQuestions(ExamEntity exam, List<QuestionEditRequest> questionRequests) {
        Map<Long, QuestionEntity> existingQuestions = exam.getQuestions()
                .stream()
                .collect(Collectors.toMap(QuestionEntity::getId, q -> q));

        List<QuestionEntity> updatedQuestions = new ArrayList<>();

        for (QuestionEditRequest req : questionRequests) {
            QuestionEntity question;
            if (req.getId() != null && existingQuestions.containsKey(req.getId())) {
                question = existingQuestions.get(req.getId());
                question.setQuestionText(req.getQuestionText());
            } else {
                question = new QuestionEntity();
                question.setExam(exam);
                question.setQuestionText(req.getQuestionText());
            }
            syncOptions(question, req.getOptions());
            updatedQuestions.add(question);
        }

        exam.getQuestions().clear();
        exam.getQuestions().addAll(updatedQuestions);
    }

    // Sync options
    private void syncOptions(QuestionEntity question, List<OptionEditRequest> optionRequests) {
        Map<Long, OptionEntity> existingOptions = question.getOptions()
                .stream()
                .collect(Collectors.toMap(OptionEntity::getId, o -> o));

        List<OptionEntity> updatedOptions = new ArrayList<>();

        for (OptionEditRequest req : optionRequests) {
            OptionEntity option;
            if (req.getId() != null && existingOptions.containsKey(req.getId())) {
                option = existingOptions.get(req.getId());
                option.setText(req.getText());
                option.setCorrect(req.isCorrect());
            } else {
                option = new OptionEntity();
                option.setQuestion(question);
                option.setText(req.getText());
                option.setCorrect(req.isCorrect());
            }
            updatedOptions.add(option);
        }

        question.getOptions().clear();
        question.getOptions().addAll(updatedOptions);
    }

    // create another similar exam
    public ExamResponse duplicateExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam Doesn't exist"));

        Long currentTeacherId = getCurrentTeacherId();

        if (!exam.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("You are not allowed to duplicate this exam");
        }

        ExamEntity copy = new ExamEntity();
        copy.setTitle(exam.getTitle() + " (Copy)");
        copy.setPublished(false);
        copy.setSubject(exam.getSubject());
        copy.setTeacherId(exam.getTeacherId());

        // Deep copy the questions and options

        List<QuestionEntity> newQuestions = exam.getQuestions()
                .stream()
                .map(q -> {
                    QuestionEntity newQ = new QuestionEntity();
                    newQ.setQuestionText(q.getQuestionText());
                    newQ.setExam(copy);

                    List<OptionEntity> newOptions = q.getOptions()
                            .stream()
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

        examRepository.save(copy);

        return toResponse(copy);
    }

    // Get teacher's exam
    public List<ExamResponse> getMyExams() {

        Long teacherId = getCurrentTeacherId();

        return examRepository.findByTeacherId(teacherId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get exams (By published, By Subject, By both, By neither)
    public List<ExamResponse> getExams(Long subjectId, Boolean published) {

        List<ExamEntity> exams;

        if (subjectId != null && published != null) {
            // Published exams by subject
            exams = examRepository.findBySubject_IdAndPublished(subjectId, published);

        } else if (subjectId != null) {
            // All exams by subject
            exams = examRepository.findBySubject_Id(subjectId);

        } else if (published != null) {
            // All published exams
            exams = examRepository.findByPublished(published);

        } else {
            // All exams
            exams = examRepository.findAll();
        }

        return exams.stream()
                .map(this::toResponse)
                .toList();
    }

    // Get particular exam
    public ExamResponse getExamById(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam doesn't exist!"));

        return toResponse(exam);
    }

    // Exam to attempt (No details of correct options)
    public ExamAttemptResponse getExamForAttempt(Long examId) {

        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (!exam.isPublished()) {
            throw new RuntimeException("Exam not published yet");
        }

        List<QuestionAttemptResponse> questionResponses =
                exam.getQuestions().stream()
                        .map(question -> QuestionAttemptResponse.builder()
                                .id(question.getId())
                                .questionText(question.getQuestionText())
                                .options(
                                        question.getOptions().stream()
                                                .map(option -> OptionAttemptResponse.builder()
                                                        .id(option.getId())
                                                        .text(option.getText())
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

}
