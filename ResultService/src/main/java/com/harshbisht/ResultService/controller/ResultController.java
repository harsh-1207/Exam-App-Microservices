package com.harshbisht.ResultService.controller;

import com.harshbisht.ResultService.dto.ResultResponse;
import com.harshbisht.ResultService.dto.SubmitExamRequest;
import com.harshbisht.ResultService.service.ResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @PostMapping("/submit")
    public ResultResponse submit(@Valid @RequestBody SubmitExamRequest request) {
        return resultService.submit(request);
    }

    @GetMapping("/my")
    public List<ResultResponse> myResults() {
        return resultService.myResults();
    }

    @GetMapping("/my/exam/{examId}")
    public ResultResponse myResultForExam(@PathVariable Long examId) {
        return resultService.myResultForExam(examId);
    }

    @GetMapping("/{id}")
    public ResultResponse get(@PathVariable Long id) {
        return resultService.getResult(id);
    }

    @GetMapping("/exam/{examId}")
    public List<ResultResponse> examResults(@PathVariable Long examId) {
        return resultService.examResults(examId);
    }
}
