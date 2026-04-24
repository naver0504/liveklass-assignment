package com.liveklass.notification.application.config;

import com.liveklass.common.event.ChannelType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "notification.retry")
public record NotificationRetryProperties(
        int defaultMaxAttempts,
        Map<ChannelType, ChannelRetryPolicy> typePolicy
) {
    private static final long DEFAULT_BACKOFF_BASE_SECONDS = 5L;

    public int maxAttempts(final ChannelType channelType) {
        if (typePolicy != null && typePolicy.containsKey(channelType)) {
            return typePolicy.get(channelType).maxAttempts();
        }
        return defaultMaxAttempts > 0 ? defaultMaxAttempts : 3;
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
