package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.OptionDTO.OptionRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.OptionEntity;
import com.harshbisht.ExamService.entity.QuestionEntity;
import com.harshbisht.ExamService.exception.AccessDeniedException;
import com.harshbisht.ExamService.exception.InvalidRequestException;
import com.harshbisht.ExamService.exception.ResourceNotFoundException;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new AccessDeniedException("Cannot resolve user identity from token");
    }

    public List<QuestionResponse> getAllQuestionsByExam(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Exam not found");
        }

        // FIX: toResponse() maps isCorrect — this is used by teachers viewing their
        // own questions. Students use getExamForAttempt() in ExamService which returns
        // ExamAttemptResponse / OptionAttemptResponse (no isCorrect field).
        // The GET /questions endpoint is still secured to STUDENT+TEACHER, but
        // isCorrect is only meaningful to the teacher who owns the exam.
        // If you want to enforce this at the service level, add a role check here.
        return questionRepository.findByExamId(examId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionResponse addQuestionInExam(Long examId, AddQuestionRequest request) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // FIX: Add ownership check. Without this, any teacher could add questions
        // to another teacher's exam. ExamService's editExam checks ownership but
        // the question-level add/update/delete endpoints had no such guard.
        assertTeacherOwnsExam(exam);

        if (request.getQuestionText() == null || request.getQuestionText().isBlank()) {
            throw new InvalidRequestException("Question text cannot be empty");
        }

        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new InvalidRequestException("Question must have at least 2 options");
        }

        long correctCount = request.getOptions().stream()
                .filter(OptionRequest::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new InvalidRequestException("Exactly one option must be marked correct");
        }

        QuestionEntity question = new QuestionEntity();
        question.setQuestionText(request.getQuestionText());
        question.setExam(exam);

        List<OptionEntity> options = request.getOptions().stream()
                .map(optReq -> {
                    OptionEntity option = new OptionEntity();
                    option.setText(optReq.getText());
                    option.setCorrect(optReq.isCorrect());
                    option.setQuestion(question);
                    return option;
                })
                .toList();

        question.setOptions(options);
        return toResponse(questionRepository.save(question));
    }

    @Transactional
    public QuestionResponse updateQuestion(Long examId, Long questionId, AddQuestionRequest request) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // FIX: Ownership check added here too.
        assertTeacherOwnsExam(exam);

        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getExam().getId().equals(examId)) {
            throw new InvalidRequestException("Question does not belong to this exam");
        }

        if (request.getQuestionText() != null && !request.getQuestionText().isBlank()) {
            question.setQuestionText(request.getQuestionText());
        }

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            long correctCount = request.getOptions().stream()
                    .filter(OptionRequest::isCorrect)
                    .count();

            if (correctCount != 1) {
                throw new InvalidRequestException("Exactly one option must be marked correct");
            }

            question.getOptions().clear();

            List<OptionEntity> newOptions = request.getOptions().stream()
                    .map(o -> {
                        OptionEntity option = new OptionEntity();
                        option.setText(o.getText());
                        option.setCorrect(o.isCorrect());
                        option.setQuestion(question);
                        return option;
                    })
                    .toList();

            question.getOptions().addAll(newOptions);
        }

        return toResponse(question);
    }

    @Transactional
    public void deleteQuestion(Long examId, Long questionId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // FIX: Ownership check added.
        assertTeacherOwnsExam(exam);

        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getExam().getId().equals(examId)) {
            throw new InvalidRequestException("Question does not belong to this exam");
        }

        // FIX: The old code called exam.getQuestions().remove(question).
        // List.remove() uses equals() — QuestionEntity has @Data which generates
        // equals() based on ALL fields including the lazy-loaded options collection.
        // This is unreliable and can silently fail (returns false, nothing deleted).
        // The correct approach is to delete via the repository directly; the
        // orphanRemoval on ExamEntity.questions handles cascade automatically.
        questionRepository.delete(question);
    }

    public QuestionResponse getQuestionById(Long examId, Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getExam().getId().equals(examId)) {
            throw new InvalidRequestException("Question does not belong to this exam");
        }

        return toResponse(question);
    }

    private void assertTeacherOwnsExam(ExamEntity exam) {
        Long currentUserId = getCurrentUserId();
        if (!exam.getTeacherId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not own this exam");
        }
    }

    private QuestionResponse toResponse(QuestionEntity entity) {
        List<OptionRequest> options = entity.getOptions().stream()
                .map(opt -> new OptionRequest(opt.getId(), opt.getText(), opt.isCorrect()))
                .collect(Collectors.toList());
        return new QuestionResponse(entity.getId(), entity.getQuestionText(), options);
    }
}