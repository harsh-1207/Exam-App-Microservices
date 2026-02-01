package com.harshbisht.ExamService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateExamRequest {
    private String title;
    private Long subjectId;
}