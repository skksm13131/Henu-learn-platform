package com.hwz.user.dto;

import java.util.List;

public class AdminLearningSummaryResponse {
    private int totalUsers;
    private int activeUsers;
    private long totalLearningItems;
    private long completedItems;
    private long totalLearningSeconds;
    private List<UserProgressSummary> userSummaries;

    public AdminLearningSummaryResponse() {}

    public AdminLearningSummaryResponse(int totalUsers, int activeUsers, long totalLearningItems,
                                       long completedItems, long totalLearningSeconds,
                                       List<UserProgressSummary> userSummaries) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalLearningItems = totalLearningItems;
        this.completedItems = completedItems;
        this.totalLearningSeconds = totalLearningSeconds;
        this.userSummaries = userSummaries;
    }

    // Getters and setters
    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }

    public long getTotalLearningItems() { return totalLearningItems; }
    public void setTotalLearningItems(long totalLearningItems) { this.totalLearningItems = totalLearningItems; }

    public long getCompletedItems() { return completedItems; }
    public void setCompletedItems(long completedItems) { this.completedItems = completedItems; }

    public long getTotalLearningSeconds() { return totalLearningSeconds; }
    public void setTotalLearningSeconds(long totalLearningSeconds) { this.totalLearningSeconds = totalLearningSeconds; }

    public List<UserProgressSummary> getUserSummaries() { return userSummaries; }
    public void setUserSummaries(List<UserProgressSummary> userSummaries) { this.userSummaries = userSummaries; }

    public static class UserProgressSummary {
        private Long userId;
        private String username;
        private String displayName;
        private long totalItems;
        private long completedItems;
        private long learningSeconds;
        private String lastActiveTime;

        public UserProgressSummary() {}

        public UserProgressSummary(Long userId, String username, String displayName,
                                 long totalItems, long completedItems, long learningSeconds,
                                 String lastActiveTime) {
            this.userId = userId;
            this.username = username;
            this.displayName = displayName;
            this.totalItems = totalItems;
            this.completedItems = completedItems;
            this.learningSeconds = learningSeconds;
            this.lastActiveTime = lastActiveTime;
        }

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

        public long getCompletedItems() { return completedItems; }
        public void setCompletedItems(long completedItems) { this.completedItems = completedItems; }

        public long getLearningSeconds() { return learningSeconds; }
        public void setLearningSeconds(long learningSeconds) { this.learningSeconds = learningSeconds; }

        public String getLastActiveTime() { return lastActiveTime; }
        public void setLastActiveTime(String lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    }
}
