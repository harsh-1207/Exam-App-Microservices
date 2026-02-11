package com.harshbisht.ExamService.dto.SubjectDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubjectResponse {
    private Long id;
    private String name;
}