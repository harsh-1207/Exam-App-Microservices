package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.CreateSubjectRequest;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectEntity>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @PostMapping
    public ResponseEntity<SubjectEntity> createSubject(@RequestBody CreateSubjectRequest request) {
        return ResponseEntity.ok(subjectService.createSubject(request));
    }
}
