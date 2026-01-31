package com.harshbisht.ExamService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionDTO {
    private String text;
    private boolean correct;
}
