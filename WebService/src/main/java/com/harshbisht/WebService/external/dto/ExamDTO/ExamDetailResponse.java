package com.harshbisht.WebService.external.dto.ExamDTO;

import com.harshbisht.WebService.external.dto.QuestionDTO.QuestionResponse;
import lombok.Data;

import java.util.List;

@Data
public class ExamDetailResponse {
    private Long id;
    private String title;
    private boolean published;
    private List<QuestionResponse> questions;
}
