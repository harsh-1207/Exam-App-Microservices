package com.harshbisht.ExamService.dto;

import lombok.Data;

@Data
public class OptionRequest {
    private String text;
    private boolean correct;
}