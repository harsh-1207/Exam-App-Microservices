package com.harshbisht.ExamService.dto.ExamDTO;

import com.harshbisht.ExamService.dto.QuestionDTO.QuestionEditRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditExamRequest {

    private String title;
    private Long subjectId;

    private List<QuestionEditRequest> questions;
}

