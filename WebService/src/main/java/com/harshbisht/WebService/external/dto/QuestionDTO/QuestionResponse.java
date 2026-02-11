package com.harshbisht.WebService.external.dto.QuestionDTO;

import com.harshbisht.WebService.external.dto.OptionDTO.OptionResponse;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<OptionResponse> options;
}
