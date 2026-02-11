package com.harshbisht.WebService.external.dto.OptionDTO;

import lombok.Data;

@Data
public class OptionResponse {
    private Long id;
    private String text;
    private boolean correct;
}
