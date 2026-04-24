package com.liveklass.notification.worker.consumer;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.domain.DomainEventOutbox;

import java.util.List;

public interface NotificationPublisher {

    ChannelType supports();

    PublishResult publish(DomainEventOutbox outbox);

    default List<PublishResult> publishBatch(final List<DomainEventOutbox> outboxes) {
        return outboxes.stream().map(this::publish).toList();
    }
}
