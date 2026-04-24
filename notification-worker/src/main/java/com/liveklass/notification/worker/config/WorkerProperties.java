package com.liveklass.notification.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification.polling")
public record WorkerProperties(
        boolean enabled,
        long fixedDelayMs,
        int batchSize,
        long stuckThresholdMinutes
) {
}
