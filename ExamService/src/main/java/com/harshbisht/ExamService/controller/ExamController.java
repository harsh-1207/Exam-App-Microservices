package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.ExamDTO.CreateExamRequest;
import com.harshbisht.ExamService.dto.ExamDTO.EditExamRequest;
import com.harshbisht.ExamService.dto.ExamDTO.ExamResponse;
import com.harshbisht.ExamService.service.ExamService;
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
    public ResponseEntity<ExamResponse> createExam(@RequestBody CreateExamRequest request) {
        return ResponseEntity.ok(examService.createExam(request));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ExamResponse>> getExamsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(examService.getExamsBySubject(subjectId));
    }

    @PutMapping("/{examId}/publish")
    public ResponseEntity<ExamResponse> publishExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.publishExam(examId));
    }

    @PutMapping("/{examId}/full")
    public ResponseEntity<ExamResponse> editFullExam(
            @PathVariable Long examId,
            @RequestBody EditExamRequest request) {
        return ResponseEntity.ok(examService.editExamFull(examId, request));
    }

    // 🔹 Delete exam
    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }
}
