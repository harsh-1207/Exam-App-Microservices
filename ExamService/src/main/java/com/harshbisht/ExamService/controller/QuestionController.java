package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse;
import com.harshbisht.ExamService.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exams/{examId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long examId,
            @Valid @RequestBody AddQuestionRequest request
    ) {
        return ResponseEntity.ok(
                questionService.addQuestionInExam(
                        examId,
                        request
                )
        );
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @Valid @RequestBody AddQuestionRequest request
    ) {
        return ResponseEntity.ok(
                questionService.updateQuestion(
                        examId,
                        questionId,
                        request
                )
        );
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId
    ) {
        questionService.deleteQuestion(
                examId,
                questionId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                questionService.getAllQuestionsByExam(examId)
        );
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestionById(
            @PathVariable Long examId,
            @PathVariable Long questionId
    ) {
        return ResponseEntity.ok(
                questionService.getQuestionById(
                        examId,
                        questionId
                )
        );
    }
}