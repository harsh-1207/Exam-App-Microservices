package com.harshbisht.ResultService.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "EXAM-SERVICE")
public interface ExamFeignClient {

}
