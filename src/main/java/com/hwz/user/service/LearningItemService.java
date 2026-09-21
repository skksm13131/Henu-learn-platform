package com.hwz.user.service;

import com.hwz.user.dto.LearningItemDetailResponse;
import com.hwz.user.dto.LearningItemListResponse;
import com.hwz.user.dto.LearningItemPreviewResponse;

import java.util.List;

public interface LearningItemService {
    List<LearningItemListResponse> listItems(String category);

    LearningItemPreviewResponse getPreview(Long id);

    LearningItemDetailResponse getDetail(Long id);
}
