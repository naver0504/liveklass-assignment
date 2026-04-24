package com.liveklass.notification.worker.consumer.policy;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.application.config.NotificationRetryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BackoffPolicy {

    private final NotificationRetryProperties retryProperties;

    public LocalDateTime nextAttemptAt(final ChannelType channelType, final int attemptCount, final LocalDateTime now) {
        final long baseSeconds = retryProperties.backoffBaseSeconds(channelType);
        final long base = baseSeconds * (1L << Math.max(0, attemptCount - 1));
        final long jitterSeconds = (long) (base * (Math.random() * 0.4 - 0.2));
        return now.plusSeconds(base + jitterSeconds);
    }
}
