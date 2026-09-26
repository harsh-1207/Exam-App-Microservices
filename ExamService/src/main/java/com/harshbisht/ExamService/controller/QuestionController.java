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
    public ResponseEntity<QuestionResponse> addQuestion(            // Add a new question to an exam
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
    public ResponseEntity<QuestionResponse> updateQuestion(      // Update an existing question in an exam
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
    public ResponseEntity<Void> deleteQuestion(                 // Delete a question from an exam
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
    public ResponseEntity<List<QuestionResponse>> getQuestions(     // Get all questions for a specific exam
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                questionService.getAllQuestionsByExam(examId)
        );
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestionById(        // Get a specific question by its ID for a specific exam
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