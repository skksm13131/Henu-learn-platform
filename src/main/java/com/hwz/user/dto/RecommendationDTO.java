package com.hwz.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecommendationDTO {
    private Long itemPk;

    private String title;
    private String category;
    private String difficulty;
    private String duration;

    private Long learnDurationSec;

    private LocalDateTime lastLearnTime;

    private Long expectedDurationSec;
}
