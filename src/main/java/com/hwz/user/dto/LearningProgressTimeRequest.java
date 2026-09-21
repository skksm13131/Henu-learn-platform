package com.hwz.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LearningProgressTimeRequest {
    @JsonProperty("item_pk")
    private Long itemPk;

    @JsonProperty("delta_sec")
    private Long deltaSec;
}
