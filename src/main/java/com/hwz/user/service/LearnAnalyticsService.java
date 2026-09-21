package com.hwz.user.service;

import com.hwz.user.dto.CategoryProgressDTO;
import com.hwz.user.dto.DashboardDTO;
import com.hwz.user.dto.DifficultyProgressDTO;
import com.hwz.user.dto.RecentItemDTO;
import com.hwz.user.dto.RecommendationDTO;
import com.hwz.user.dto.SummaryDTO;
import com.hwz.user.dto.TrendPointDTO;

import java.util.List;

public interface LearnAnalyticsService {
    DashboardDTO getDashboard(Long userId);

    SummaryDTO getSummary(Long userId);

    List<CategoryProgressDTO> getByCategory(Long userId);

    List<DifficultyProgressDTO> getByDifficulty(Long userId);

    List<TrendPointDTO> getCompletionTrend(Long userId, Integer days);

    List<RecentItemDTO> getRecent(Long userId, Integer limit);

    RecommendationDTO getRecommendation(Long userId);
}
