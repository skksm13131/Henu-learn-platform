package com.hwz.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningProgressTimeResponse {
    @JsonProperty("item_pk")
    private Long itemPk;

    @JsonProperty("learn_duration_sec")
    private Long learnDurationSec;
}
