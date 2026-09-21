package com.hwz.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearningItemDetailResponse {
    private Long id;
    private String title;
    private String summary;
    private String category;
    private String difficulty;
    private String duration;
    private String prerequisites;
    private String objectives;
    private List<LearningFeature> features;
    private List<LearningStepResponse> steps;
}
