package com.harshbisht.ExamService.dto.OptionDTO;

import lombok.Data;

@Data
public class OptionEditRequest {

    private Long id; // NULL = new option
    private String text;
    private boolean correct;
}
