package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.SubjectDTO.CreateSubjectRequest;
import com.harshbisht.ExamService.dto.SubjectDTO.SubjectResponse;
import com.harshbisht.ExamService.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody CreateSubjectRequest request
    ) {
        return ResponseEntity.ok(
                subjectService.createSubject(request)
        );
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long subjectId,
            @Valid @RequestBody CreateSubjectRequest request
    ) {
        return ResponseEntity.ok(
                subjectService.editSubject(
                        subjectId,
                        request
                )
        );
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long subjectId
    ) {
        subjectService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {
        return ResponseEntity.ok(
                subjectService.getAllSubjects()
        );
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> getSubject(
            @PathVariable Long subjectId
    ) {
        return ResponseEntity.ok(
                subjectService.getSubject(subjectId)
        );
    }
}