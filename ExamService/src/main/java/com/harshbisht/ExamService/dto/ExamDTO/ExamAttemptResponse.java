package com.harshbisht.ExamService.dto.ExamDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ExamAttemptResponse {

    private Long id;
    private String title;
    private List<QuestionAttemptResponse> questions;

}

