package com.harshbisht.ExamService.dto.ExamDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuestionAttemptResponse {

    private Long id;
    private String questionText;
    private List<OptionAttemptResponse> options;

}
