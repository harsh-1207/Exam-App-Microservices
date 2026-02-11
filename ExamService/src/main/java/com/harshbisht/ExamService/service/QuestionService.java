package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse;
import com.harshbisht.ExamService.dto.OptionDTO.OptionRequest;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.OptionEntity;
import com.harshbisht.ExamService.entity.QuestionEntity;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    // 🔹 Get all questions of an exam → return DTOs
    public List<QuestionResponse> getAllQuestionsByExam(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new RuntimeException("Exam does not exist");
        }

        return questionRepository.findByExamId(examId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Add question to an exam → return DTO
    @Transactional
    public QuestionResponse addQuestionInExam(Long examId, AddQuestionRequest request) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam does not exist"));

        if (request.getQuestionText() == null || request.getQuestionText().isBlank()) {
            throw new RuntimeException("Question text cannot be empty");
        }

        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new RuntimeException("Question must have at least 2 options");
        }

        long correctCount = request.getOptions().stream()
                .filter(OptionRequest::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new RuntimeException("Exactly one option must be correct");
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

        QuestionEntity saved = questionRepository.save(question);
        return toResponse(saved);
    }

    // 🔹 Mapper: Entity → DTO
    private QuestionResponse toResponse(QuestionEntity entity) {
        List<OptionRequest> options = entity.getOptions().stream()
                .map(opt -> new OptionRequest(opt.getId(), opt.getText(), opt.isCorrect()))
                .collect(Collectors.toList());

        return new QuestionResponse(entity.getId(), entity.getQuestionText(), options);
    }
}
