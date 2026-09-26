package com.harshbisht.WebService.external.dto.ExamDTO;

import com.harshbisht.WebService.external.dto.QuestionDTO.QuestionEditRequest;
import lombok.Data;

import java.util.List;

@Data
public class EditExamRequest {

    private String title;
    private Long subjectId;
    private Boolean published;

    private List<QuestionEditRequest> questions;
}
