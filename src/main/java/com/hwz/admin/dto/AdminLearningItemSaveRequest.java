package com.hwz.admin.dto;

import com.hwz.user.dto.LearningFeature;
import lombok.Data;

import java.util.List;

@Data
public class AdminLearningItemSaveRequest {
    private String title;
    private String summary;
    private String category;
    private String difficulty;
    private String duration;
    private String prerequisites;
    private List<String> objectives;
    private List<LearningFeature> features;
    private List<StepInput> steps;

    @Data
    public static class StepInput {
        private Integer stepNo;
        private String title;
        private String description;
        private String tip;
        private String code;
    }
}
