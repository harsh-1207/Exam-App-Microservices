package com.harshbisht.WebService.external.dto.OptionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionRequest {
    private Long id;
    private String text;
    private boolean correct;
}
