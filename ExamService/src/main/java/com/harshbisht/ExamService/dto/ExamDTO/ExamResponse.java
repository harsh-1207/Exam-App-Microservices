package com.harshbisht.ExamService.dto.ExamDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ExamResponse {
    private Long id;
    private String title;
    private boolean published;
}