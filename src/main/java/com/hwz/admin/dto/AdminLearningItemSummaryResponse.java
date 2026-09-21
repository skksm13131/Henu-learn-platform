package com.hwz.admin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AdminLearningItemSummaryResponse {
    Long id;
    String title;
    String category;
    String difficulty;
    String duration;
    String status;
    Long authorId;
    Integer stepCount;
    boolean templateAvailable;
    LocalDateTime publishedAt;
    LocalDateTime updatedAt;
}
