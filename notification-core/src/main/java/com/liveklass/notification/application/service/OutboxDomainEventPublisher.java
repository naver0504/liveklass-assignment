package com.liveklass.notification.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.common.event.PayloadValidator;
import com.liveklass.notification.application.config.NotificationRetryProperties;
import com.liveklass.notification.domain.vo.IdempotencyKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final OutboxService outboxService;
    private final NotificationRetryProperties retryProperties;

    @Override
    public void publish(final DomainEvent event) {
        final ChannelType channelType = event.topic().getChannelType();
        final JsonNode payload = event.payload();
        PayloadValidator.validate(channelType, payload);
        outboxService.createOutbox(
                IdempotencyKey.of(event.topic(), event.recipientId(), channelType, event.referenceId()).value(),
                event.recipientId(),
                event.recipientId(),
                event.topic(),
                channelType,
                event.referenceId(),
                payload.toString(),
                event.publishedAt(),
                retryProperties.maxAttempts(channelType)
        );
    }
}
