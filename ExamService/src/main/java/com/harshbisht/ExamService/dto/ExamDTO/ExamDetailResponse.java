package com.harshbisht.ExamService.dto.ExamDTO;

import com.harshbisht.ExamService.dto.QuestionDTO.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamDetailResponse {
    private Long id;
    private String title;
    private boolean published;
    private List<QuestionResponse> questions;
}
