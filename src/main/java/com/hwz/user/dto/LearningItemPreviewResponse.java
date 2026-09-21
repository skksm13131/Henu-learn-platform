package com.hwz.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningItemPreviewResponse {
    private Long id;
    private String title;
    private String summary;
    private String difficulty;
    private String duration;
    private String prerequisites;
    private String objectives;
}
