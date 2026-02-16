package com.harshbisht.ExamService.dto.QuestionDTO;

import com.harshbisht.ExamService.dto.OptionDTO.OptionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<OptionRequest> options;
}