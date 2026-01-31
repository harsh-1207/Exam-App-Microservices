package com.harshbisht.ExamService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamResponse {
    private Long id;
    private String title;
    private boolean published;
}

