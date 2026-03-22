package com.harshbisht.ResultService.controller;

import com.harshbisht.ResultService.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    // APIs

    /*
    Accepting Exam Submissions
    Scoring Logic
    Prevent Double Submission
    Fetch Student’s Results
    Fetch Result For Particular Exam
    Teacher View – View Results of Their Exam

    Student

    POST /results/submit
    GET /results/my
    GET /results/exam/{examId}

    Teacher
    GET /results/exam/{examId}/all
    */
//    @PostMapping("/submit")
//    public ExamSubmitResponse submitExam(@RequestBody ExamSubmitRequest request) {
//        return resultService.submitExam(request);
//    }
//
//    @GetMapping("/my")
//    public List<ResultResponse> GetAllResults () {
//        // get studentId by security context
//        return resultService.getAllResults();
//    }
//
//    @GetMapping("exam/{examId}")
//    public ResultResponse getResult(@PathVariable Long examId) {
//        return resultService.getResult(examId);
//    }
//
//    @GetMapping("/exam/{examId}/all")
//    public List<ResultResponse> getResultOfStudentsByExam(@PathVariable Long examId) {
//        return resultService.getResultOfStudentsByExam(examId);
//    }
}
