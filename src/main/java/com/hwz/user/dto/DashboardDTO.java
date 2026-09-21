package com.hwz.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardDTO {
    private SummaryDTO summary;

    private List<CategoryProgressDTO> byCategory;

    private List<DifficultyProgressDTO> byDifficulty;

    private List<TrendPointDTO> completionTrend;

    private List<RecentItemDTO> recent;

    private RecommendationDTO recommendation;
}
