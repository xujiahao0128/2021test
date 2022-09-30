package com.wangzaiplus.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月06日 18:00
 */
@Slf4j
@Configuration
public class TaskExecutorConfig {

    @Bean(value = "myTaskExecutor")
    public Executor myTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("xujiahao-");
        executor.setMaxPoolSize(50);
        executor.setCorePoolSize(30);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(30);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

}
