package com.harshbisht.WebService.service;

import com.harshbisht.WebService.external.ExamFeignClient;
import com.harshbisht.WebService.external.dto.ExamDTO.CreateExamRequest;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamDetailResponse;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.OptionDTO.OptionRequest;
import com.harshbisht.WebService.external.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.WebService.external.dto.SubjectDTO.CreateSubjectRequest;
import com.harshbisht.WebService.external.dto.SubjectDTO.SubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final ExamFeignClient examFeign;

    public List<SubjectResponse> getAllSubjects() {
        return examFeign.getAllSubjects();
    }

    public SubjectResponse createSubject(String name) {
        // Build the DTO here
        CreateSubjectRequest request = new CreateSubjectRequest();
        request.setName(name);

        // Delegate to Feign client
        return examFeign.createSubject(request);
    }

    public SubjectResponse getSubject(Long subjectId) {
        SubjectResponse subject = examFeign.getSubject(subjectId);

        if (subject == null) {
            throw new RuntimeException("Subject not found with id: " + subjectId);
        }

        return subject;
    }

    public List<ExamResponse> getExamsBySubject(Long subjectId) {
        List<ExamResponse> exams = examFeign.getExamsBySubject(subjectId);
        return exams != null ? exams : List.of();
    }

    public ExamDetailResponse getExamWithQuestions(Long examId) {
        return examFeign.getExamWithQuestions(examId);
    }

    public ExamResponse createExam(Long subjectId, String title) {
        CreateExamRequest request = new CreateExamRequest(title, subjectId);

        return examFeign.createExam(request).getBody();
    }
    public void addQuestion(Long examId,
                            String questionText,
                            List<OptionRequest> options) {

        AddQuestionRequest request = AddQuestionRequest.builder()
                .examId(examId)
                .questionText(questionText)
                .options(options)
                .build();

        examFeign.addQuestion(examId, request);
    }


}