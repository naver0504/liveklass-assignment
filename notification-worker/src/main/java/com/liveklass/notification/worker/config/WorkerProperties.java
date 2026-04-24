package com.liveklass.notification.worker.config;

import com.liveklass.common.event.ChannelType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "notification")
public record WorkerProperties(
        PollingProperties polling,
        RetryProperties retry
) {

    public record PollingProperties(
            boolean enabled,
            long fixedDelayMs,
            int batchSize,
            long stuckThresholdMinutes
    ) {}

    public record RetryProperties(
            int defaultMaxAttempts,
            Map<ChannelType, ChannelRetryPolicy> typePolicy
    ) {
        private static final long DEFAULT_BACKOFF_BASE_SECONDS = 5L;

        public int maxAttempts(final ChannelType channelType) {
            if (typePolicy != null && typePolicy.containsKey(channelType)) {
                return typePolicy.get(channelType).maxAttempts();
            }
            return defaultMaxAttempts;
        }

        public long backoffBaseSeconds(final ChannelType channelType) {
            if (typePolicy != null && typePolicy.containsKey(channelType)) {
                return typePolicy.get(channelType).backoffBaseSeconds();
            }
            return DEFAULT_BACKOFF_BASE_SECONDS;
        }

        public record ChannelRetryPolicy(
                int maxAttempts,
                long backoffBaseSeconds
        ) {}
    }
}
