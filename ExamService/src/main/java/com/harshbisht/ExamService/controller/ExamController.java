package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.ExamDTO.*;
import com.harshbisht.ExamService.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ExamResponse> createExam(             // Create a new exam
            @Valid @RequestBody CreateExamRequest request
    ) {
        return ResponseEntity.ok(
                examService.createExam(request)
        );
    }

    @PutMapping("/{examId}/full")
    public ResponseEntity<ExamResponse> editExam(               // Edit an existing exam
            @PathVariable Long examId,
            @Valid @RequestBody EditExamRequest request
    ) {
        return ResponseEntity.ok(
                examService.editExam(examId, request)
        );
    }

    @PutMapping("/{examId}/publish")
    public ResponseEntity<ExamResponse> publishExam(            // Publish an exam
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                examService.publishExam(examId)
        );
    }

    @PutMapping("/{examId}/unpublish")
    public ResponseEntity<ExamResponse> unPublishExam(          // Unpublish an exam
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                examService.unPublishExam(examId)
        );
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(                     // Delete an exam
            @PathVariable Long examId
    ) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{examId}/duplicate")
    public ResponseEntity<ExamResponse> duplicateExam(          // Duplicate an exam
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                examService.duplicateExam(examId)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<ExamResponse>> getMyExams() {      // Get exams created by the current user
        return ResponseEntity.ok(
                examService.getMyExams()
        );
    }

    @GetMapping
    public ResponseEntity<List<ExamResponse>> getExams(         // Get exams with optional filters for subject and published status
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Boolean published
    ) {
        return ResponseEntity.ok(
                examService.getExams(subjectId, published)
        );
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponse> getExamById(            // Get a specific exam by its ID
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                examService.getExamById(examId)
        );
    }

    @GetMapping("/{examId}/attempt")
    public ResponseEntity<ExamAttemptResponse> getExamForAttempt(       // Get exam details for attempting the exam
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                examService.getExamForAttempt(examId)
        );
    }

    @GetMapping("/{examId}/full")
    public ResponseEntity<ExamDetailResponse> getExamWithQuestions(     // Get full exam details including questions
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                examService.getExamWithQuestions(examId)
        );
    }

    @GetMapping("/{examId}/evaluation")
    public ResponseEntity<com.harshbisht.ExamService.dto.internal.ExamEvaluationResponse> getEvaluationData(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getEvaluationData(examId));
    }
}