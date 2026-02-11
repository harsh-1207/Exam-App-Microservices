package com.harshbisht.WebService.external.dto.ExamDTO;

import com.harshbisht.WebService.external.dto.QuestionDTO.QuestionEditRequest;
import lombok.Data;

import java.util.List;

@Data
public class EditExamRequest {

    private String title;
    private Long subjectId;

    private List<QuestionEditRequest> questions;
}
