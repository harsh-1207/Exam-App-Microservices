package com.harshbisht.WebService.external.dto.QuestionDTO;

import com.harshbisht.WebService.external.dto.OptionDTO.OptionEditRequest;
import lombok.Data;

import java.util.List;

@Data
public class QuestionEditRequest {

    private Long id; // NULL = new question
    private String questionText;
    private Integer marks;

    private List<OptionEditRequest> options;
}

