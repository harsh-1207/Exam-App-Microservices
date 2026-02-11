package com.harshbisht.ExamService.dto.SubjectDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubjectRequest {
    private String name;
}