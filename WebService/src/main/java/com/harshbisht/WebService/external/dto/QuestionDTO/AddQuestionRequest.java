package com.harshbisht.WebService.external.dto.QuestionDTO;

import com.harshbisht.WebService.external.dto.OptionDTO.OptionRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddQuestionRequest {
    private Long examId;
    private String questionText;
    private List<OptionRequest> options;  // exactly 4
}
