package com.hwz.assignment.controller;

import com.hwz.admin.service.AdminAccessService;
import com.hwz.assignment.dto.AssignmentDtos;
import com.hwz.assignment.entity.AssignmentMaterial;
import com.hwz.assignment.entity.AssignmentSubmissionFile;
import com.hwz.assignment.service.AssignmentService;
import com.hwz.common.PageResponse;
import com.hwz.common.Result;
import com.hwz.common.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AdminAccessService accessService;

    public AssignmentController(AssignmentService assignmentService, AdminAccessService accessService) {
        this.assignmentService = assignmentService;
        this.accessService = accessService;
    }

    @GetMapping
    public Result<PageResponse<AssignmentDtos.AssignmentSummary>> list(@RequestParam(required = false) String keyword,
                                                                       @RequestParam(defaultValue = "1") long page,
                                                                       @RequestParam(defaultValue = "10") long pageSize) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.pageStudent(user.getId(), keyword, normalizePage(page), normalizePageSize(pageSize)));
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats(@RequestParam(required = false) String keyword) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.studentStats(user.getId(), keyword));
    }

    @GetMapping("/{assignmentId}")
    public Result<AssignmentDtos.AssignmentDetail> detail(@PathVariable Long assignmentId) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.getStudentDetail(assignmentId, user.getId()));
    }

    @PostMapping("/{assignmentId}/submissions")
    public Result<AssignmentDtos.SubmissionDetail> createSubmission(@PathVariable Long assignmentId) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.createOrGetSubmission(assignmentId, user));
    }

    @PostMapping("/{assignmentId}/files")
    public Result<AssignmentDtos.SubmissionDetail> uploadFile(@PathVariable Long assignmentId,
                                                              @RequestParam("fileType") String fileType,
                                                              @RequestParam("file") MultipartFile file) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.uploadFile(assignmentId, user, fileType, file));
    }

    @PutMapping("/{assignmentId}/answer")
    public Result<AssignmentDtos.SubmissionDetail> saveAnswer(@PathVariable Long assignmentId,
                                                              @RequestBody(required = false) AssignmentDtos.AnswerSaveRequest request) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.saveAnswer(assignmentId, user, request));
    }

    @PostMapping("/{assignmentId}/submit")
    public Result<AssignmentDtos.SubmissionDetail> submit(@PathVariable Long assignmentId,
                                                          @RequestBody(required = false) AssignmentDtos.AnswerSaveRequest request) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.submit(assignmentId, user, request));
    }

    @GetMapping("/submissions/{submissionId}")
    public Result<AssignmentDtos.SubmissionDetail> submission(@PathVariable Long submissionId) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.getStudentSubmission(submissionId, user.getId()));
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        User user = accessService.requireUser();
        AssignmentSubmissionFile file = assignmentService.getFileForDownload(fileId, user, false);
        Resource resource = assignmentService.loadFile(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(file.getOriginalName()))
                .body(resource);
    }

    @DeleteMapping("/files/{fileId}")
    public Result<AssignmentDtos.SubmissionDetail> deleteFile(@PathVariable Long fileId) {
        User user = accessService.requireUser();
        return Result.ok(assignmentService.deleteStudentFile(fileId, user));
    }

    @GetMapping("/materials/{materialId}/download")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long materialId) {
        User user = accessService.requireUser();
        AssignmentMaterial material = assignmentService.getMaterialForDownload(materialId, user, false);
        Resource resource = assignmentService.loadMaterial(material);
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentType(resolveMediaType(material.getMimeType()));
        if ("DOCUMENT".equalsIgnoreCase(material.getMaterialType())) {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(material.getOriginalName()));
        }
        return builder.body(resource);
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

    private String contentDisposition(String fileName) {
        try {
            return "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return "attachment";
        }
    }

    private MediaType resolveMediaType(String mimeType) {
        try {
            return mimeType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(mimeType);
        } catch (IllegalArgumentException ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
