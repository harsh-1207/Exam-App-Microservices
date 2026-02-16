package com.harshbisht.ExamService.dto.OptionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionRequest {
    private Long id;
    private String text;
    private boolean correct;
}
