package com.liveklass.notification.worker.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.common.event.PayloadValidator;
import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.domain.vo.IdempotencyKey;
import com.liveklass.notification.worker.config.WorkerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DomainEventPublisherAdapter implements DomainEventPublisher {

    private final OutboxService outboxService;
    private final WorkerProperties workerProperties;

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
                LocalDateTime.now(),
                workerProperties.retry().maxAttempts(channelType)
        );
    }
}
