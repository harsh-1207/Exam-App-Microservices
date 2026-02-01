package com.harshbisht.ExamService.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddQuestionRequest {
    private Long examId;
    private String questionText;
    private List<OptionRequest> options;  // exactly 4
}