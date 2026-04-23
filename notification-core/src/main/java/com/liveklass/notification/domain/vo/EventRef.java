package com.liveklass.notification.domain.vo;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;

import java.util.Objects;

public record EventRef(
        Topic topic,
        ChannelType channelType,
        String referenceId
) {
    public EventRef {
        Objects.requireNonNull(topic,       "topic must not be null");
        Objects.requireNonNull(channelType, "channelType must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
    }
}
