package com.liveklass.notification.infrastructure.mapper;

import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.domain.vo.ProcessingLock;
import com.liveklass.notification.domain.vo.RetryState;
import com.liveklass.notification.infrastructure.persistence.entity.OutboxJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OutboxEntityMapper {

    public DomainEventOutbox toDomain(final OutboxJpaEntity entity) {
        return DomainEventOutbox.builder()
                .id(new OutboxId(entity.getId()))
                .idempotencyKey(entity.getIdempotencyKey())
                .requesterId(entity.getRequesterId())
                .recipientId(entity.getRecipientId())
                .eventRef(new EventRef(entity.getTopic(), entity.getChannelType(), entity.getReferenceId()))
                .payload(entity.getPayload())
                .lock(ProcessingLock.of(entity.getStatus(), entity.getLockedAt()))
                .retryState(new RetryState(
                        entity.getAttemptCount(),
                        entity.getMaxAttempts(),
                        entity.getNextAttemptAt(),
                        entity.getLastError()
                ))
                .build();
    }
}
