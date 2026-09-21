package com.hwz.user.dto;

import lombok.Data;

@Data
public class DifficultyProgressDTO {
    private String difficulty;

    private Long completedCount;

    private Long totalCount;

    private Long learnDurationSec;
}
