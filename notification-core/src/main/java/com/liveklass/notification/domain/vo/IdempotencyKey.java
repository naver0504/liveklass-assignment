package com.liveklass.notification.domain.vo;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;

import java.util.Objects;

public record IdempotencyKey(String value) {

    public IdempotencyKey {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static IdempotencyKey of(
            final Topic topic,
            final Long recipientId,
            final ChannelType channelType,
            final String referenceId
    ) {
        Objects.requireNonNull(topic,       "topic must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(channelType, "channelType must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        return new IdempotencyKey(topic.name() + ":" + recipientId + ":" + channelType.name() + ":" + referenceId);
    }
}
