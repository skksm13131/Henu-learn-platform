package com.hwz.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LearningProgressResponse {
    private Long id;
    private Long userId;
    private Long itemPk;
    private LocalDateTime firstLearnTime;
    private LocalDateTime completeTime;
    private String completeRemark;
    private Long learnDurationSec;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
