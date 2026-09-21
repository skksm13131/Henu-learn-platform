package com.hwz.assignment.service;

import com.hwz.assignment.dto.AssignmentDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;

/**
 * 批量下载任务管理：内存任务表 + 后台线程池异步生成 ZIP。
 *
 * <p>下载任务为短生命周期临时对象，进程重启即失效可接受，因此用 {@link ConcurrentHashMap}
 * 保存，不引入数据库表。READY 的 ZIP 保留到 TTL，由 {@link #cleanupExpiredTasks()} 兜底清理，
 * 而不是下载结束立即删除（浏览器中断 / 重连 / ticket 过期后重新申请都还能继续下载）。
 */
@Service
public class BatchDownloadTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchDownloadTaskService.class);

    /** PREPARING / FAILED 任务的最长保留时间，超时视为卡死，清理时移除。 */
    private static final long MAX_PREPARING_MILLIS = 30 * 60_000L;

    private final Map<String, BatchDownloadTask> tasks = new ConcurrentHashMap<>();
    private final AssignmentService assignmentService;
    private final DownloadTempStore downloadTempStore;
    private final ThreadPoolTaskExecutor executor;

    public BatchDownloadTaskService(AssignmentService assignmentService,
                                    DownloadTempStore downloadTempStore,
                                    @Qualifier("batchDownloadExecutor") ThreadPoolTaskExecutor executor) {
        this.assignmentService = assignmentService;
        this.downloadTempStore = downloadTempStore;
        this.executor = executor;
    }

    public BatchDownloadTask createTask(Long userId, Long assignmentId, String statusFilter) {
        BatchDownloadTask task = new BatchDownloadTask(
                UUID.randomUUID().toString().replace("-", ""), userId, assignmentId, statusFilter);
        task.setStatus(BatchDownloadTask.STATUS_PREPARING);
        task.setTotalFiles((int) assignmentService.countSubmissionFilesForZip(assignmentId, statusFilter));
        tasks.put(task.getDownloadId(), task);
        try {
            executor.submit(() -> runTask(task));
        } catch (RejectedExecutionException ex) {
            task.setStatus(BatchDownloadTask.STATUS_FAILED);
            task.setErrorMessage("下载任务排队已满，请稍后重试");
            LOGGER.warn("批量下载任务提交被拒绝 downloadId={}", task.getDownloadId());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "下载任务排队已满，请稍后重试");
        }
        return task;
    }

    public BatchDownloadTask getOwnedTask(String downloadId, Long userId) {
        BatchDownloadTask task = tasks.get(downloadId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "下载任务不存在或已过期");
        }
        if (userId == null || !userId.equals(task.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问该下载任务");
        }
        return task;
    }

    public BatchDownloadTask getReadyTask(String downloadId, Long userId) {
        BatchDownloadTask task = getOwnedTask(downloadId, userId);
        if (!BatchDownloadTask.STATUS_READY.equals(task.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "下载文件尚未生成完成");
        }
        if (!StringUtils.hasText(task.getFilePath()) || !Files.exists(Paths.get(task.getFilePath()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "下载文件不存在或已过期");
        }
        return task;
    }

    public AssignmentDtos.DownloadTaskResponse toResponse(BatchDownloadTask task) {
        return AssignmentDtos.DownloadTaskResponse.builder()
                .downloadId(task.getDownloadId())
                .status(task.getStatus())
                .processedFiles(task.getProcessedFiles())
                .totalFiles(task.getTotalFiles())
                .fileName(task.getFileName())
                .fileSize(task.getFileSize())
                .message(task.getErrorMessage())
                .build();
    }

    private void runTask(BatchDownloadTask task) {
        try {
            // 打包前再次校验（附件数 / 上限 / 磁盘空间），防止预检后磁盘被其他任务占用
            assignmentService.assertSubmissionFilesZipDownloadable(task.getAssignmentId(), task.getStatusFilter());
            File zip = assignmentService.buildSubmissionFilesZip(
                    task.getAssignmentId(), task.getStatusFilter(),
                    (processed, total) -> {
                        task.setProcessedFiles(processed);
                        task.setTotalFiles(total);
                    });
            task.setFilePath(zip.getAbsolutePath());
            task.setFileName(assignmentService.submissionFilesZipName(task.getAssignmentId()));
            task.setFileSize(zip.length());
            task.setCompletedAt(System.currentTimeMillis());
            task.setExpiresAt(System.currentTimeMillis() + downloadTempStore.getTtlMillis());
            task.setStatus(BatchDownloadTask.STATUS_READY);
        } catch (Exception ex) {
            LOGGER.error("批量下载 ZIP 生成失败 downloadId={}", task.getDownloadId(), ex);
            task.setStatus(BatchDownloadTask.STATUS_FAILED);
            task.setErrorMessage(resolveBuildError(ex));
        }
    }

    private String resolveBuildError(Exception ex) {
        if (ex instanceof ResponseStatusException) {
            String reason = ((ResponseStatusException) ex).getReason();
            if (StringUtils.hasText(reason)) {
                return reason;
            }
        }
        return "生成下载文件失败";
    }

    @Scheduled(fixedDelayString = "${labcore.assignment.download-temp-cleanup-interval-ms:3600000}")
    public void cleanupExpiredTasks() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, BatchDownloadTask> entry : tasks.entrySet()) {
            BatchDownloadTask task = entry.getValue();
            boolean expired;
            if (BatchDownloadTask.STATUS_READY.equals(task.getStatus())) {
                expired = task.getExpiresAt() > 0 && task.getExpiresAt() < now;
            } else {
                expired = task.getCreatedAt() + MAX_PREPARING_MILLIS < now;
            }
            if (expired) {
                tasks.remove(entry.getKey());
                deleteZip(task.getFilePath());
                LOGGER.info("清理过期下载任务 downloadId={}", task.getDownloadId());
            }
        }
    }

    private void deleteZip(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ex) {
            LOGGER.warn("清理下载临时文件失败: {}", filePath, ex);
        }
    }
}
