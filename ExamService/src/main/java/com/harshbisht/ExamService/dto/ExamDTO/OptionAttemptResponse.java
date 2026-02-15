package com.harshbisht.ExamService.dto.ExamDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OptionAttemptResponse {

    private Long id;
    private String text;
}
