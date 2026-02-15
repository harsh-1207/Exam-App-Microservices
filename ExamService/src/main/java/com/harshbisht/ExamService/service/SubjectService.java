package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.SubjectDTO.CreateSubjectRequest;
import com.harshbisht.ExamService.dto.SubjectDTO.SubjectResponse;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public SubjectResponse createSubject(CreateSubjectRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Subject name cannot be empty");
        }

        String currSubject = request.getName().trim();

        if (subjectRepository.existsByNameIgnoreCase(currSubject)) {
            throw new RuntimeException("Subject Already Exists");
        }

        SubjectEntity subject = new SubjectEntity();
        subject.setName(currSubject);

        return mapToDTO(subjectRepository.save(subject));
    }

    public SubjectResponse editSubject(Long subjectId, CreateSubjectRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Subject name cannot be empty");
        }

        String currSubject = request.getName().trim();

        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject Not Found"));

        if (subjectRepository.existsByNameIgnoreCase(currSubject)
                && !subject.getName().equalsIgnoreCase(currSubject)) {
            throw new RuntimeException("Subject name already exists");
        }

        subject.setName(currSubject);

        return mapToDTO(subjectRepository.save(subject));
    }

    public void deleteSubject(Long subjectId) {

        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getExams().isEmpty()) {
            throw new RuntimeException("Cannot delete subject with exams");
        }

        subjectRepository.delete(subject);
    }

    private SubjectResponse mapToDTO(SubjectEntity subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .build();
    }

    public SubjectResponse getSubject(Long subjectId) {
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        return new SubjectResponse(subject.getId(), subject.getName());
    }

}