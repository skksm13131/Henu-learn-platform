package com.hwz.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

    /**
     * 批量下载 ZIP 后台构建线程池。
     *
     * <p>教学平台并发管理员很低，线程池刻意设小：最多同时 2 个构建、队列最多 4 个，
     * 防止管理员连续点击多个超大下载把服务器 CPU / 磁盘打满。
     */
    @Bean("batchDownloadExecutor")
    public ThreadPoolTaskExecutor batchDownloadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(4);
        executor.setThreadNamePrefix("batch-download-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}
