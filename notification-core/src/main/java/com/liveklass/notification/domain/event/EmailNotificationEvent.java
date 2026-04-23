package com.liveklass.notification.domain.event;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;

import java.time.LocalDateTime;
import java.util.Objects;

public record EmailNotificationEvent(
        Topic topic,
        Long recipientId,
        String referenceId,
        String subject,
        String body,
        String recipientEmail,
        LocalDateTime publishedAt
) {
    public EmailNotificationEvent {
        Objects.requireNonNull(topic,          "topic must not be null");
        Objects.requireNonNull(recipientId,    "recipientId must not be null");
        Objects.requireNonNull(referenceId,    "referenceId must not be null");
        Objects.requireNonNull(subject,        "subject must not be null");
        Objects.requireNonNull(body,           "body must not be null");
        Objects.requireNonNull(recipientEmail, "recipientEmail must not be null");
        Objects.requireNonNull(publishedAt,    "publishedAt must not be null");
    }

    public ChannelType channelType() {
        return ChannelType.EMAIL;
    }
}
