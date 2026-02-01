package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.CreateExamRequest;
import com.harshbisht.ExamService.entity.ExamEntity;
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
    public ResponseEntity<ExamEntity> createExam(@RequestBody CreateExamRequest request) {
        return ResponseEntity.ok(examService.createExam(request));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ExamEntity>> getExamsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(examService.getExamsBySubject(subjectId));
    }

    @PutMapping("/{examId}/publish")
    public ResponseEntity<ExamEntity> publishExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.publishExam(examId));
    }
}
