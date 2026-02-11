package com.harshbisht.ExamService.dto.QuestionDTO;

import com.harshbisht.ExamService.dto.OptionDTO.OptionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<OptionRequest> options;
}