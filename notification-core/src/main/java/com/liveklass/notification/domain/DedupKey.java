package com.liveklass.notification.domain;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.exception.OutboxException;

import java.util.Objects;

public record DedupKey(String value) {

    private static final int MAX_LENGTH = 512;

    public DedupKey {
        Objects.requireNonNull(value, "value must not be null");
        if (value.length() > MAX_LENGTH) {
            throw ExceptionCreator.create(OutboxException.INVALID_DEDUP_KEY,
                    "length: " + value.length() + ", max: " + MAX_LENGTH);
        }
    }

    public static DedupKey of(
            final Topic topic,
            final Long recipientId,
            final ChannelType channelType,
            final String referenceId
    ) {
        Objects.requireNonNull(topic,       "topic must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(channelType, "channelType must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        return new DedupKey(topic.name() + ":" + recipientId + ":" + channelType.name() + ":" + referenceId);
    }
}
