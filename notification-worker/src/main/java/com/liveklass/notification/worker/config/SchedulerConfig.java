package com.liveklass.notification.worker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(WorkerProperties.class)
public class SchedulerConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean(name = "emailPublisherExecutor")
    public Executor emailPublisherExecutor() {
        final int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(256);
        executor.setThreadNamePrefix("email-publisher-");
        executor.initialize();
        return executor;
    }
}
