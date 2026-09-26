package com.harshbisht.ResultService.external;

import com.harshbisht.ResultService.config.FeignConfig;
import com.harshbisht.ResultService.dto.ExamEvaluationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "EXAM-SERVICE", configuration = FeignConfig.class)
public interface ExamFeignClient {
	@GetMapping("/exams/{examId}/evaluation")
	ExamEvaluationResponse getEvaluationData(@PathVariable Long examId);
}
