package com.hwz.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentItemDTO {
    private Long itemPk;

    private String title;
    private String category;
    private String difficulty;

    private Long learnDurationSec;

    private LocalDateTime updatedAt;

    private LocalDateTime completeTime;

    private String completeRemark;
}
