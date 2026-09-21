package com.hwz.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LearningTemplateStorageService {

    private static final String CLASSPATH_PREFIX = "classpath:";

    private final Path storageRoot;
    private final String classpathRoot;

    public LearningTemplateStorageService(
            @Value("${labcore.learning.template-storage-dir:classpath:learning-templates}") String storageDir
    ) {
        if (storageDir != null && storageDir.startsWith(CLASSPATH_PREFIX)) {
            this.classpathRoot = trimSlashes(storageDir.substring(CLASSPATH_PREFIX.length()));
            this.storageRoot = null;
        } else {
            this.classpathRoot = null;
            this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
        }
    }

    public String storeTemplate(Long itemId, MultipartFile file) throws IOException {
        if (itemId == null || itemId <= 0) {
            throw new IOException("学习项编号不正确");
        }
        if (file == null || file.isEmpty()) {
            throw new IOException("请选择实验模板文件");
        }
        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName) || !originalName.toLowerCase().endsWith(".ipynb")) {
            throw new IOException("仅支持 .ipynb 格式的实验模板");
        }
        if (isClasspathStorage()) {
            throw new IOException("当前模板目录为只读资源目录，请配置可写的模板存储目录后再上传");
        }

        Path targetDir = storageRoot.resolve("item-" + itemId);
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve("template.ipynb");
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return storageRoot.relativize(targetFile).toString().replace('\\', '/');
    }

    public boolean templateExists(String templatePath) {
        if (!StringUtils.hasText(templatePath)) {
            return false;
        }
        if (isClasspathStorage()) {
            return resolveClasspathResource(templatePath).exists();
        }
        return Files.exists(resolveFilePath(templatePath));
    }

    public Resource loadTemplate(String templatePath) {
        if (isClasspathStorage()) {
            Resource resource = resolveClasspathResource(templatePath);
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "\u6a21\u677f\u6587\u4ef6\u4e0d\u5b58\u5728");
            }
            return resource;
        }
        return new FileSystemResource(resolveFilePath(templatePath));
    }

    private Path resolveFilePath(String templatePath) {
        if (!StringUtils.hasText(templatePath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\u6a21\u677f\u8def\u5f84\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Path relativePath = Paths.get(templatePath);
        if (relativePath.isAbsolute()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\u6a21\u677f\u8def\u5f84\u4e0d\u5408\u6cd5");
        }
        Path resolvedPath = storageRoot.resolve(relativePath).normalize();
        if (!resolvedPath.startsWith(storageRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\u6a21\u677f\u8def\u5f84\u4e0d\u5408\u6cd5");
        }
        return resolvedPath;
    }

    private Resource resolveClasspathResource(String templatePath) {
        if (!StringUtils.hasText(templatePath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\u6a21\u677f\u8def\u5f84\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String normalized = templatePath.replace('\\', '/');
        if (normalized.startsWith("/") || normalized.contains("../") || normalized.equals("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\u6a21\u677f\u8def\u5f84\u4e0d\u5408\u6cd5");
        }
        return new ClassPathResource(classpathRoot + "/" + normalized);
    }

    private boolean isClasspathStorage() {
        return classpathRoot != null;
    }

    private String trimSlashes(String value) {
        String result = value == null ? "" : value.trim().replace('\\', '/');
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
