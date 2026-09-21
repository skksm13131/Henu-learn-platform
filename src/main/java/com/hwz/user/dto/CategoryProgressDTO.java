package com.hwz.user.dto;

import lombok.Data;

@Data
public class CategoryProgressDTO {
    private String category;

    private Long completedCount;

    private Long totalCount;

    private Long learnDurationSec;
}
