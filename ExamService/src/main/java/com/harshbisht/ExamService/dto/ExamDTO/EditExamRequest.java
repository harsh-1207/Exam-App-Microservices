package com.harshbisht.ExamService.dto.ExamDTO;

import com.harshbisht.ExamService.dto.QuestionDTO.QuestionEditRequest;
import lombok.Data;

import java.util.List;

@Data
public class EditExamRequest {

    private String title;
    private Long subjectId;

    private List<QuestionEditRequest> questions;
}

