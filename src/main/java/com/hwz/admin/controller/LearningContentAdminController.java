package com.hwz.admin.controller;

import com.hwz.admin.dto.AdminLearningItemDetailResponse;
import com.hwz.admin.dto.AdminLearningItemSaveRequest;
import com.hwz.admin.dto.AdminLearningItemSummaryResponse;
import com.hwz.admin.service.AdminAccessService;
import com.hwz.admin.service.LearningContentAdminService;
import com.hwz.common.PageResponse;
import com.hwz.common.Result;
import com.hwz.common.entity.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/learning-items")
public class LearningContentAdminController {

    private final LearningContentAdminService learningContentAdminService;
    private final AdminAccessService accessService;

    public LearningContentAdminController(LearningContentAdminService learningContentAdminService,
                                          AdminAccessService accessService) {
        this.learningContentAdminService = learningContentAdminService;
        this.accessService = accessService;
    }

    @GetMapping
    public Result<PageResponse<AdminLearningItemSummaryResponse>> list(@RequestParam(required = false) String keyword,
                                                                       @RequestParam(required = false) String status,
                                                                       @RequestParam(required = false) String category,
                                                                       @RequestParam(defaultValue = "1") long page,
                                                                       @RequestParam(defaultValue = "10") long pageSize) {
        accessService.requireAdmin();
        return Result.ok(learningContentAdminService.pageItems(keyword, status, category, normalizePage(page), normalizePageSize(pageSize)));
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String category) {
        accessService.requireAdmin();
        return Result.ok(learningContentAdminService.stats(keyword, status, category));
    }

    @GetMapping("/{itemId}")
    public Result<AdminLearningItemDetailResponse> detail(@PathVariable Long itemId) {
        accessService.requireAdmin();
        return Result.ok(learningContentAdminService.getDetail(itemId));
    }

    @PostMapping
    public Result<AdminLearningItemDetailResponse> create(@RequestBody AdminLearningItemSaveRequest request) {
        User operator = accessService.requireAdmin();
        return Result.ok(learningContentAdminService.createItem(request, operator));
    }

    @PutMapping("/{itemId}")
    public Result<AdminLearningItemDetailResponse> update(@PathVariable Long itemId,
                                                          @RequestBody AdminLearningItemSaveRequest request) {
        accessService.requireAdmin();
        return Result.ok(learningContentAdminService.updateItem(itemId, request));
    }

    @PutMapping("/{itemId}/status")
    public Result<AdminLearningItemDetailResponse> updateStatus(@PathVariable Long itemId,
                                                                @RequestBody Map<String, String> payload) {
        accessService.requireAdmin();
        String status = payload == null ? null : payload.get("status");
        return Result.ok(learningContentAdminService.updateStatus(itemId, status));
    }

    @PostMapping("/{itemId}/template")
    public Result<AdminLearningItemDetailResponse> uploadTemplate(@PathVariable Long itemId,
                                                                  @RequestParam("file") MultipartFile file) {
        accessService.requireAdmin();
        return Result.ok(learningContentAdminService.uploadTemplate(itemId, file));
    }

    private long normalizePage(long page) {
        return page < 1 ? 1 : page;
    }

    private long normalizePageSize(long pageSize) {
        if (pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
