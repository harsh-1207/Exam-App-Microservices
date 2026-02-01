package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.AddQuestionRequest;
import com.harshbisht.ExamService.dto.OptionRequest;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.OptionEntity;
import com.harshbisht.ExamService.entity.QuestionEntity;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    // Get all questions of an exam
    public List<QuestionEntity> getAllQuestionsByExam(Long examId) {

        if (!examRepository.existsById(examId)) {
            throw new RuntimeException("Exam does not exist");
        }

        return questionRepository.findByExamId(examId);
    }

    // Add question to an exam
    @Transactional
    public QuestionEntity addQuestionInExam(Long examId, AddQuestionRequest request) {

        // Validate exam
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam does not exist"));

        // Validate question text
        if (request.getQuestionText() == null || request.getQuestionText().isBlank()) {
            throw new RuntimeException("Question text cannot be empty");
        }

        // Validate options count
        if (request.getOptions() == null || request.getOptions().size() != 1) {
            throw new RuntimeException("Question must have more than 1 options");
        }

        // Ensure exactly one correct option
        long correctCount = request.getOptions().stream()
                .filter(OptionRequest::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new RuntimeException("Exactly one option must be correct");
        }

        // Create Question
        QuestionEntity question = new QuestionEntity();
        question.setQuestionText(request.getQuestionText());
        question.setExam(exam);

        // Create Options and link to question
        List<OptionEntity> options = request.getOptions().stream()
                .map(optReq -> {
                    OptionEntity option = new OptionEntity();
                    option.setText(optReq.getText());
                    option.setCorrect(optReq.isCorrect());
                    option.setQuestion(question); // relationship
                    return option;
                })
                .toList();

        // Set options in question (important for JPA)
        question.setOptions(options);

        // Single save (cascade handles options)
        return questionRepository.save(question);
    }
}
