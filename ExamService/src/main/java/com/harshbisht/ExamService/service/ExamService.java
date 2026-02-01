package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.CreateExamRequest;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    public List<ExamEntity> getExamsBySubject(Long subjectId) {
        // check if subject exists
        if(!subjectRepository.existsById(subjectId)){
            throw new RuntimeException("Subject Not Found");
        }

        // get all
        return examRepository.findBySubjectId(subjectId);
    }

    public ExamEntity createExam(CreateExamRequest request) {
        // check if subject exists
        SubjectEntity subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject Not Found"));

        ExamEntity exam = new ExamEntity();
        exam.setTitle(request.getTitle());
        exam.setSubject(subject);
        exam.setPublished(false); // not visible to students yet

        return examRepository.save(exam);
    }

    public ExamEntity publishExam(Long examId) {
        // get the exam
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // change the published switch and save
        exam.setPublished(true);
        return examRepository.save(exam);
    }
}
