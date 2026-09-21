package com.hwz.user.service;

import com.hwz.user.dto.LearningProgressResponse;
import com.hwz.user.dto.LearningProgressTimeResponse;
import com.hwz.user.dto.AdminLearningSummaryResponse;
import com.hwz.user.dto.UserProgressDetailResponse;

import java.util.List;

public interface LearningProgressService {
    List<LearningProgressResponse> listByUserAndItem(Long userId, Long itemPk);

    List<LearningProgressResponse> listByUser(Long userId);

    LearningProgressResponse createFirstEntry(Long userId, Long itemPk);

    LearningProgressResponse completeLearning(Long userId, Long itemPk, String completeRemark);

    LearningProgressTimeResponse updateLearnDuration(Long userId, Long itemPk, Long deltaSec);

    AdminLearningSummaryResponse getAdminSummary();

    UserProgressDetailResponse getUserProgressDetail(Long userId);
}
