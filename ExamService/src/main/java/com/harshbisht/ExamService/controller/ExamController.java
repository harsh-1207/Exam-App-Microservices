package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.ExamDTO.CreateExamRequest;
import com.harshbisht.ExamService.dto.ExamDTO.EditExamRequest;
import com.harshbisht.ExamService.dto.ExamDTO.ExamAttemptResponse;
import com.harshbisht.ExamService.dto.ExamDTO.ExamResponse;
import com.harshbisht.ExamService.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    // TEACHER APIs

    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@RequestBody CreateExamRequest request) {
        return ResponseEntity.ok(examService.createExam(request));
    }

    @PutMapping("/{examId}/full")
    public ResponseEntity<ExamResponse> editExam(
            @PathVariable Long examId,
            @RequestBody EditExamRequest request) {

        return ResponseEntity.ok(examService.editExam(examId, request));
    }

    @PutMapping("/{examId}/publish")
    public ResponseEntity<ExamResponse> publishExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.publishExam(examId));
    }

    @PutMapping("/{examId}/unpublish")
    public ResponseEntity<ExamResponse> unPublishExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.unPublishExam(examId));
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{examId}/duplicate")
    public ResponseEntity<ExamResponse> duplicateExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.duplicateExam(examId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ExamResponse>> getMyExams() {
        // get teacher id from securityContext
        return ResponseEntity.ok(examService.getMyExams());
    }

    // COMMON APIs

    @GetMapping
    public ResponseEntity<List<ExamResponse>> getExams(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Boolean published) {

        return ResponseEntity.ok(
                examService.getExams(subjectId, published)
        );
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    // STUDENT API

    @GetMapping("/{examId}/attempt")
    public ResponseEntity<ExamAttemptResponse> getExamForAttempt(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamForAttempt(examId));
    }
}
