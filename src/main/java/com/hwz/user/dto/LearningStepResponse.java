package com.hwz.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningStepResponse {
    @JsonProperty("step_no")
    private Integer stepNo;
    private String title;
    private String description;
    private String tip;
    private String code;
}
