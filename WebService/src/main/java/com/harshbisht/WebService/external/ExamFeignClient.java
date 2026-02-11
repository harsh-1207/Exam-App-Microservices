package com.harshbisht.WebService.external;

import com.harshbisht.WebService.config.FeignConfig;
import com.harshbisht.WebService.external.dto.ExamDTO.CreateExamRequest;
import com.harshbisht.WebService.external.dto.ExamDTO.EditExamRequest;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamDetailResponse;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.WebService.external.dto.QuestionDTO.QuestionResponse;
import com.harshbisht.WebService.external.dto.SubjectDTO.CreateSubjectRequest;
import com.harshbisht.WebService.external.dto.SubjectDTO.SubjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "EXAM-SERVICE", configuration = FeignConfig.class)
public interface ExamFeignClient {

    // *********************************************  SubjectController APIs  *********************************************
    @GetMapping("/subjects")
    List<SubjectResponse> getAllSubjects();

    @PostMapping("/subjects")
    SubjectResponse createSubject(@RequestBody CreateSubjectRequest request);

    @PutMapping("/subjects/{subjectId}")
    public SubjectResponse editSubject(@PathVariable Long subjectId, @RequestBody CreateSubjectRequest request);

    @DeleteMapping("/subjects/{subjectId}")
    public void deleteSubject(@PathVariable Long subjectId);

    @GetMapping("/subjects/{id}")
    SubjectResponse getSubject(@PathVariable Long id);

    // *********************************************************************************************************************


    // *********************************************  ExamController APIs  *********************************************
    @PostMapping("/exams")
    public ResponseEntity<ExamResponse> createExam(@RequestBody CreateExamRequest request);

    @GetMapping("/exams/subject/{subjectId}")
    List<ExamResponse> getExamsBySubject(@PathVariable Long subjectId);

    @GetMapping("/exams/{examId}/full")
    ExamDetailResponse getExamWithQuestions(@PathVariable Long examId);

    @PutMapping("/exams/{examId}/publish")
    public ResponseEntity<ExamResponse> publishExam(@PathVariable Long examId);

    @PutMapping("/exams/{examId}/full")
    public ResponseEntity<ExamResponse> editFullExam(
            @PathVariable Long examId,
            @RequestBody EditExamRequest request);

    // 🔹 Delete exam
    @DeleteMapping("/exams/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId);

    // *********************************************************************************************************************


    // *********************************************  QuestionController APIs  *********************************************

    @GetMapping("/exams/{examId}/questions")
    public List<QuestionResponse> getQuestions(@PathVariable Long examId);

    @PostMapping("/exams/{examId}/questions")
    public QuestionResponse addQuestion(@PathVariable Long examId,
                                        @RequestBody AddQuestionRequest request);

    // *********************************************************************************************************************
}

