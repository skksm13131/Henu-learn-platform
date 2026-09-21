package com.hwz.assignment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 批量下载临时 ZIP 的专用存储：负责落盘目录、磁盘空间检查与过期文件的兜底清理。
 *
 * <p>临时 ZIP 独立放在专用目录，不与学生提交、上传目录、日志混在一起；正常下载结束后由
 * 调用方删除，进程异常退出等原因遗留的文件由 {@link #cleanupExpired()} 定期兜底清理。
 */
@Component
public class DownloadTempStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadTempStore.class);

    /** 批量下载临时 ZIP 的统一文件名前缀，清理任务只扫描该前缀，避免误删其它文件。 */
    public static final String FILE_PREFIX = "labcore-submissions-";

    private final Path directory;
    private final long ttlHours;
    private final long minFreeBytes;

    public DownloadTempStore(
            @Value("${labcore.assignment.download-temp-dir:data/assignment-download-temp}") String dir,
            @Value("${labcore.assignment.download-temp-ttl-hours:6}") long ttlHours,
            @Value("${labcore.assignment.download-temp-min-free-bytes:1073741824}") long minFreeBytes) {
        this.directory = Paths.get(dir).toAbsolutePath().normalize();
        this.ttlHours = ttlHours;
        this.minFreeBytes = minFreeBytes;
    }

    public Path getDirectory() {
        return directory;
    }

    /** 临时文件 TTL（毫秒），供下载任务计算 READY 过期时间。 */
    public long getTtlMillis() {
        return ttlHours * 3600_000L;
    }

    /** 在专用目录下创建唯一临时 ZIP 文件（并发安全，不会互相覆盖）。 */
    public File createTempZip() throws IOException {
        Files.createDirectories(directory);
        return Files.createTempFile(directory, FILE_PREFIX, ".zip").toFile();
    }

    /** 判断临时目录所在文件系统是否有足够空间存放待打包内容（含安全余量）。 */
    public boolean hasEnoughSpace(long totalBytes) {
        try {
            Files.createDirectories(directory);
            long usable = Files.getFileStore(directory).getUsableSpace();
            return usable >= Math.max(totalBytes, 0) + minFreeBytes;
        } catch (IOException ex) {
            LOGGER.warn("检查下载临时目录可用空间失败: {}", directory, ex);
            return false;
        }
    }

    public void ensureEnoughSpace(long totalBytes) {
        if (!hasEnoughSpace(totalBytes)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "服务器临时存储空间不足，当前无法生成批量下载文件，请稍后重试或联系管理员。");
        }
    }

    /** 兜底清理：删除超过 TTL 的遗留临时 ZIP，单个失败只记 WARN，不影响其它文件。 */
    @Scheduled(fixedDelayString = "${labcore.assignment.download-temp-cleanup-interval-ms:3600000}")
    public void cleanupExpired() {
        if (!Files.isDirectory(directory)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, FILE_PREFIX + "*.zip")) {
            for (Path file : stream) {
                try {
                    long ageMillis = System.currentTimeMillis() - Files.getLastModifiedTime(file).toMillis();
                    if (ageMillis > ttlHours * 3600_000L) {
                        if (Files.deleteIfExists(file)) {
                            LOGGER.info("清理过期下载临时文件: {}", file);
                        }
                    }
                } catch (IOException ex) {
                    LOGGER.warn("清理下载临时文件失败: {}", file, ex);
                }
            }
        } catch (IOException ex) {
            LOGGER.warn("扫描下载临时目录失败: {}", directory, ex);
        }
    }
}
