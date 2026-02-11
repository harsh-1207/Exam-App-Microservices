package com.harshbisht.WebService.external.dto.ExamDTO;

import lombok.Data;

@Data
public class ExamResponse {
    private Long id;
    private String title;
    private boolean published;
}
