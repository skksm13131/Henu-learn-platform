package com.hwz.admin.dto;

import com.hwz.user.dto.LearningFeature;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class AdminLearningItemDetailResponse {
    Long id;
    Long jsonId;
    String title;
    String summary;
    String category;
    String difficulty;
    String duration;
    String prerequisites;
    List<String> objectives;
    List<LearningFeature> features;
    List<StepDetail> steps;
    String status;
    String templatePath;
    boolean templateAvailable;
    Long authorId;
    LocalDateTime publishedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Value
    @Builder
    public static class StepDetail {
        Integer stepNo;
        String title;
        String description;
        String tip;
        String code;
    }
}
