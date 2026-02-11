package com.harshbisht.WebService.external.dto.ExamDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateExamRequest {
    private String title;
    private Long subjectId;
}
