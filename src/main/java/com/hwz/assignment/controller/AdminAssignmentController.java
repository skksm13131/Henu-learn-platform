package com.hwz.assignment.controller;

import com.hwz.admin.service.AdminAccessService;
import com.hwz.assignment.dto.AssignmentDtos;
import com.hwz.assignment.entity.AssignmentMaterial;
import com.hwz.assignment.entity.AssignmentSubmissionFile;
import com.hwz.assignment.service.AssignmentService;
import com.hwz.assignment.service.BatchDownloadTask;
import com.hwz.assignment.service.BatchDownloadTaskService;
import com.hwz.common.PageResponse;
import com.hwz.common.Result;
import com.hwz.common.auth.DownloadTicketService;
import com.hwz.common.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@RequestMapping("/admin/assignments")
public class AdminAssignmentController {

    private final AssignmentService assignmentService;
    private final AdminAccessService accessService;
    private final DownloadTicketService downloadTicketService;
    private final BatchDownloadTaskService batchDownloadTaskService;

    public AdminAssignmentController(AssignmentService assignmentService,
                                     AdminAccessService accessService,
                                     DownloadTicketService downloadTicketService,
                                     BatchDownloadTaskService batchDownloadTaskService) {
        this.assignmentService = assignmentService;
        this.accessService = accessService;
        this.downloadTicketService = downloadTicketService;
        this.batchDownloadTaskService = batchDownloadTaskService;
    }

    @GetMapping
    public Result<PageResponse<AssignmentDtos.AssignmentSummary>> list(@RequestParam(required = false) String keyword,
                                                                       @RequestParam(required = false) String status,
                                                                       @RequestParam(defaultValue = "1") long page,
                                                                       @RequestParam(defaultValue = "10") long pageSize) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.pageAdmin(keyword, status, normalizePage(page), normalizePageSize(pageSize)));
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String status) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.adminStats(keyword, status));
    }

    @GetMapping("/{assignmentId}")
    public Result<AssignmentDtos.AssignmentDetail> detail(@PathVariable Long assignmentId) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.getAdminDetail(assignmentId));
    }

    @PostMapping
    public Result<AssignmentDtos.AssignmentDetail> create(@RequestBody AssignmentDtos.AssignmentSaveRequest request) {
        User operator = accessService.requireAdmin();
        return Result.ok(assignmentService.create(request, operator));
    }

    @PutMapping("/{assignmentId}")
    public Result<AssignmentDtos.AssignmentDetail> update(@PathVariable Long assignmentId,
                                                          @RequestBody AssignmentDtos.AssignmentSaveRequest request) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.update(assignmentId, request));
    }

    @PutMapping("/{assignmentId}/status")
    public Result<AssignmentDtos.AssignmentDetail> updateStatus(@PathVariable Long assignmentId,
                                                                @RequestBody Map<String, String> payload) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.updateStatus(assignmentId, payload == null ? null : payload.get("status")));
    }

    @PostMapping("/{assignmentId}/materials")
    public Result<AssignmentDtos.MaterialDetail> uploadMaterial(@PathVariable Long assignmentId,
                                                                @RequestParam("materialType") String materialType,
                                                                @RequestParam(required = false) String title,
                                                                @RequestParam("file") MultipartFile file) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.uploadMaterial(assignmentId, materialType, title, file));
    }

    @DeleteMapping("/materials/{materialId}")
    public Result<Void> deleteMaterial(@PathVariable Long materialId) {
        accessService.requireAdmin();
        assignmentService.deleteMaterial(materialId);
        return Result.ok(null);
    }

    @GetMapping("/{assignmentId}/submissions")
    public Result<PageResponse<AssignmentDtos.SubmissionSummary>> submissions(@PathVariable Long assignmentId,
                                                                              @RequestParam(required = false) String status,
                                                                              @RequestParam(defaultValue = "1") long page,
                                                                              @RequestParam(defaultValue = "10") long pageSize) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.pageSubmissions(assignmentId, status, normalizePage(page), normalizePageSize(pageSize)));
    }

    @PostMapping("/{assignmentId}/submissions/files/download-tasks")
    public Result<AssignmentDtos.DownloadTaskResponse> createDownloadTask(@PathVariable Long assignmentId,
                                                                          @RequestParam(required = false) String status) {
        User admin = accessService.requireAdmin();
        assignmentService.assertSubmissionFilesZipDownloadable(assignmentId, status);
        BatchDownloadTask task = batchDownloadTaskService.createTask(admin.getId(), assignmentId, status);
        return Result.ok(batchDownloadTaskService.toResponse(task));
    }

    @GetMapping("/{assignmentId}/submissions/files/download-tasks/{downloadId}")
    public Result<AssignmentDtos.DownloadTaskResponse> downloadTask(@PathVariable Long assignmentId,
                                                                    @PathVariable String downloadId) {
        User admin = accessService.requireAdmin();
        BatchDownloadTask task = batchDownloadTaskService.getOwnedTask(downloadId, admin.getId());
        if (!task.getAssignmentId().equals(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "下载任务不存在或已过期");
        }
        return Result.ok(batchDownloadTaskService.toResponse(task));
    }

    @PostMapping("/{assignmentId}/submissions/files/download-tasks/{downloadId}/ticket")
    public Result<AssignmentDtos.DownloadTicketResponse> downloadTaskTicket(@PathVariable Long assignmentId,
                                                                            @PathVariable String downloadId) {
        User admin = accessService.requireAdmin();
        BatchDownloadTask task = batchDownloadTaskService.getReadyTask(downloadId, admin.getId());
        if (!task.getAssignmentId().equals(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "下载任务不存在或已过期");
        }
        String ticket = downloadTicketService.issue(admin.getId(), task.getAssignmentId(), downloadId);
        return Result.ok(AssignmentDtos.DownloadTicketResponse.builder()
                .ticket(ticket)
                .expiresInSeconds(downloadTicketService.getTtlSeconds())
                .build());
    }

    @GetMapping("/{assignmentId}/submissions/files/download/{downloadId}")
    public ResponseEntity<StreamingResponseBody> downloadTaskFile(@PathVariable Long assignmentId,
                                                                  @PathVariable String downloadId,
                                                                  @RequestParam(required = false) String ticket,
                                                                  @RequestHeader(value = "Authorization", required = false) String authorization) throws IOException {
        BatchDownloadTask task;
        if (StringUtils.hasText(authorization)) {
            User admin = accessService.requireAdmin();
            task = batchDownloadTaskService.getReadyTask(downloadId, admin.getId());
        } else {
            DownloadTicketService.DownloadTicket downloadTicket = downloadTicketService.verifyScoped(ticket, assignmentId, downloadId);
            task = batchDownloadTaskService.getReadyTask(downloadId, downloadTicket.getAdminUserId());
        }
        if (!task.getAssignmentId().equals(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "下载任务不存在或已过期");
        }
        File zip = new File(task.getFilePath());
        long zipSize = zip.length();
        StreamingResponseBody body = outputStream -> {
            try (InputStream in = new FileInputStream(zip)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) >= 0) {
                    outputStream.write(buffer, 0, len);
                }
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .contentLength(zipSize)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(task.getFileName()))
                .body(body);
    }

    @GetMapping("/{assignmentId}/submissions/files/download-info")
    public Result<AssignmentDtos.BatchDownloadInfo> submissionFilesDownloadInfo(@PathVariable Long assignmentId,
                                                                                @RequestParam(required = false) String status) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.getSubmissionFilesBatchDownloadInfo(assignmentId, status));
    }

    @GetMapping("/submissions/{submissionId}")
    public Result<AssignmentDtos.SubmissionDetail> submission(@PathVariable Long submissionId) {
        accessService.requireAdmin();
        return Result.ok(assignmentService.getAdminSubmission(submissionId));
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public Result<AssignmentDtos.SubmissionDetail> grade(@PathVariable Long submissionId,
                                                         @RequestBody AssignmentDtos.GradeRequest request) {
        User operator = accessService.requireAdmin();
        return Result.ok(assignmentService.grade(submissionId, request, operator));
    }

    @PostMapping("/submissions/{submissionId}/return")
    public Result<AssignmentDtos.SubmissionDetail> returnSubmission(@PathVariable Long submissionId,
                                                                    @RequestBody AssignmentDtos.GradeRequest request) {
        User operator = accessService.requireAdmin();
        return Result.ok(assignmentService.returnSubmission(submissionId, request, operator));
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        User user = accessService.requireAdmin();
        AssignmentSubmissionFile file = assignmentService.getFileForDownload(fileId, user, true);
        Resource resource = assignmentService.loadFile(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(file.getOriginalName()))
                .body(resource);
    }

    @GetMapping("/materials/{materialId}/download")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long materialId) {
        User user = accessService.requireAdmin();
        AssignmentMaterial material = assignmentService.getMaterialForDownload(materialId, user, true);
        Resource resource = assignmentService.loadMaterial(material);
        return ResponseEntity.ok()
                .contentType(resolveMediaType(material.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(material.getOriginalName()))
                .body(resource);
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
