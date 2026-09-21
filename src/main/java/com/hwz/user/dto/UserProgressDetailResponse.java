package com.hwz.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDetailResponse {

    // 用户学习概览
    private int totalItems;          // 总学习项目数
    private int completedItems;      // 已完成项目数
    private int inProgressItems;     // 进行中项目数
    private long totalLearningTime;  // 总学习时长（秒）

    // 详细的学习项目列表
    private List<LearningItemProgress> learningItems;

    // 最近学习活动
    private List<RecentActivity> recentActivities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningItemProgress {
        private Long itemId;
        private String itemName;
        private String category;
        private int progress;          // 进度百分比 0-100
        private String status;         // COMPLETED, IN_PROGRESS, NOT_STARTED
        private long learningTime;     // 学习时长（秒）
        private String lastAccessTime; // 最后访问时间
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private Long id;
        private String itemName;
        private String action;         // "开始学习", "继续学习", "完成学习" 等
        private long duration;         // 学习时长（秒）
        private String timestamp;      // 活动时间
    }
}
