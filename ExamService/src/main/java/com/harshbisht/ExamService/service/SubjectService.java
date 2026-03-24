package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.SubjectDTO.CreateSubjectRequest;
import com.harshbisht.ExamService.dto.SubjectDTO.SubjectResponse;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.exception.InvalidRequestException;
import com.harshbisht.ExamService.exception.ResourceNotFoundException;
import com.harshbisht.ExamService.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public SubjectResponse createSubject(CreateSubjectRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("Subject name cannot be empty");
        }

        String name = request.getName().trim();

        if (subjectRepository.existsByNameIgnoreCase(name)) {
            throw new InvalidRequestException("Subject already exists");
        }

        SubjectEntity subject = new SubjectEntity();
        subject.setName(name);
        return mapToDTO(subjectRepository.save(subject));
    }

    public SubjectResponse editSubject(Long subjectId, CreateSubjectRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("Subject name cannot be empty");
        }

        String name = request.getName().trim();

        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        if (subjectRepository.existsByNameIgnoreCase(name)
                && !subject.getName().equalsIgnoreCase(name)) {
            throw new InvalidRequestException("Subject name already exists");
        }

        subject.setName(name);
        return mapToDTO(subjectRepository.save(subject));
    }

    public void deleteSubject(Long subjectId) {
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        if (!subject.getExams().isEmpty()) {
            throw new InvalidRequestException("Cannot delete a subject that has exams");
        }

        subjectRepository.delete(subject);
    }

    public SubjectResponse getSubject(Long subjectId) {
        return mapToDTO(
                subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new ResourceNotFoundException("Subject not found"))
        );
    }

    private SubjectResponse mapToDTO(SubjectEntity subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .build();
    }
}