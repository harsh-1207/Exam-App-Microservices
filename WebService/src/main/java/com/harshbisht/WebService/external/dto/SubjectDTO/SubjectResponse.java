package com.harshbisht.WebService.external.dto.SubjectDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResponse {
    private Long id;
    private String name;
}
