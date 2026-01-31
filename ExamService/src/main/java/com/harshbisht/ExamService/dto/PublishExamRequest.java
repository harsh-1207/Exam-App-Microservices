package com.harshbisht.ExamService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishExamRequest {
    private Long examId;
}

