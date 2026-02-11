package com.harshbisht.WebService.service;

import com.harshbisht.WebService.external.ExamFeignClient;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamDetailResponse;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.SubjectDTO.SubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final ExamFeignClient examFeign;

    // 📚 Student sees all subjects
    public List<SubjectResponse> getAllSubjects() {
        return examFeign.getAllSubjects();
    }

    // 📝 Student sees exams of a subject (ONLY published ones ideally)
    public List<ExamResponse> getExamsBySubject(Long subjectId) {
        return examFeign.getExamsBySubject(subjectId);
    }

    // ❓ Student opens an exam to attempt (questions + options)
    public ExamDetailResponse getExamWithQuestions(Long examId) {
        return examFeign.getExamWithQuestions(examId);
    }

//    public Object getPublishedExamsBySubject(Long subjectId) {
//        return null;
//    }
//
//    public Object getExamForStudent(Long examId) {
//    }
}


