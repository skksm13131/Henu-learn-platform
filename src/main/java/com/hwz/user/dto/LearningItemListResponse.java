package com.hwz.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningItemListResponse {
    private Long id;
    private String title;
    private String summary;
    private String category;
}
