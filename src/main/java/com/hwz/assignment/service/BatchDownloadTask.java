package com.hwz.assignment.service;

/**
 * 内存中的批量下载任务对象，短生命周期，进程重启即失效可接受。
 *
 * <p>状态由后台构建线程单写、HTTP 轮询线程多读，可变字段全部用 volatile 保证可见性。
 */
public class BatchDownloadTask {

    public static final String STATUS_PREPARING = "PREPARING";
    public static final String STATUS_READY = "READY";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_EXPIRED = "EXPIRED";

    private final String downloadId;
    private final Long userId;
    private final Long assignmentId;
    private final String statusFilter;
    private final long createdAt;

    private volatile String status;
    private volatile int processedFiles;
    private volatile int totalFiles;
    private volatile String filePath;
    private volatile String fileName;
    private volatile long fileSize;
    private volatile String errorMessage;
    private volatile long completedAt;
    private volatile long expiresAt;

    public BatchDownloadTask(String downloadId, Long userId, Long assignmentId, String statusFilter) {
        this.downloadId = downloadId;
        this.userId = userId;
        this.assignmentId = assignmentId;
        this.statusFilter = statusFilter;
        this.createdAt = System.currentTimeMillis();
    }

    public String getDownloadId() {
        return downloadId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProcessedFiles() {
        return processedFiles;
    }

    public void setProcessedFiles(int processedFiles) {
        this.processedFiles = processedFiles;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
