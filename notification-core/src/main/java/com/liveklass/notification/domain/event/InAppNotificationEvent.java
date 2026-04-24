package com.liveklass.notification.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.InAppPayload;
import com.liveklass.common.event.Topic;

import java.time.LocalDateTime;
import java.util.Objects;

public record InAppNotificationEvent(
        Topic topic,
        Long recipientId,
        String referenceId,
        String title,
        String body,
        LocalDateTime publishedAt
) implements DomainEvent {

    public InAppNotificationEvent {
        Objects.requireNonNull(topic,       "topic must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        Objects.requireNonNull(title,       "title must not be null");
        Objects.requireNonNull(body,        "body must not be null");
        Objects.requireNonNull(publishedAt, "publishedAt must not be null");
    }

    public ChannelType channelType() {
        return ChannelType.IN_APP;
    }

    @Override
    public JsonNode payload() {
        return InAppPayload.builder(title, body)
                .metadata("publishedAt", publishedAt.toString())
                .build();
    }
}
