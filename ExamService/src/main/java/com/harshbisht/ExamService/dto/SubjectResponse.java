package com.harshbisht.ExamService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class SubjectResponse {
    private Long id;
    private String name;
}