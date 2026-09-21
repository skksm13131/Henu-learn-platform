package com.hwz.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SummaryDTO {
    private Double completionRate;

    private Long completedCount;

    private Long totalCount;

    private Long totalCourseCount;

    private Long inProgressCount;

    private Long learnDurationSec;

    private LocalDateTime lastActiveTime;
}
