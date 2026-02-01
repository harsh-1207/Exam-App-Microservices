package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.CreateSubjectRequest;
import com.harshbisht.ExamService.dto.SubjectResponse;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<SubjectEntity> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public SubjectEntity createSubject(CreateSubjectRequest request) {
        // check for existence
        String currSubject = request.getName().trim();
        Boolean exists = subjectRepository.existsByNameIgnoreCase(currSubject);

        if(!exists) {
            throw new RuntimeException("Subject Already Exists");
        }

        // create the subject
        SubjectEntity subject = new SubjectEntity();
        subject.setName(currSubject);
        return subjectRepository.save(subject);
    }
}
