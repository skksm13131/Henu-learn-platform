package com.hwz.user.controller;

import com.hwz.user.dto.LearningItemDetailResponse;
import com.hwz.user.dto.LearningItemListResponse;
import com.hwz.user.dto.LearningItemPreviewResponse;
import com.hwz.common.Result;
import com.hwz.user.service.LearningItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning-items")
public class LearningItemController {

    private final LearningItemService learningItemService;

    public LearningItemController(LearningItemService learningItemService) {
        this.learningItemService = learningItemService;
    }

    @GetMapping
    public Result<List<LearningItemListResponse>> list(@RequestParam(required = false) String category) {
        return Result.ok(learningItemService.listItems(category));
    }

    @GetMapping("/{id}/preview")
    public Result<LearningItemPreviewResponse> preview(@PathVariable Long id) {
        return Result.ok(learningItemService.getPreview(id));
    }

    @GetMapping("/{id}")
    public Result<LearningItemDetailResponse> detail(@PathVariable Long id) {
        return Result.ok(learningItemService.getDetail(id));
    }

}
