package com.harshbisht.ExamService.controller;

import com.harshbisht.ExamService.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse;
import com.harshbisht.ExamService.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exams/{examId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    // 🔹 Get all questions of an exam
    @GetMapping
    public List<QuestionResponse> getQuestions(@PathVariable Long examId) {
        return questionService.getAllQuestionsByExam(examId);
    }

    // 🔹 Add question to exam
    @PostMapping
    public QuestionResponse addQuestion(@PathVariable Long examId,
                                        @RequestBody AddQuestionRequest request) {
        return questionService.addQuestionInExam(examId, request);
    }
}
