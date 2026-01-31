package com.harshbisht.ExamService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<OptionDTO> options;
}

