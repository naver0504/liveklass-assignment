package com.liveklass.notification.application.repository;

import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.id.OutboxId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    DomainEventOutbox save(DomainEventOutbox outbox);

    List<DomainEventOutbox> saveAll(List<DomainEventOutbox> outboxes);

    Optional<DomainEventOutbox> findById(OutboxId id);

    List<DomainEventOutbox> findTop50PendingBefore(LocalDateTime scheduledAt);

    List<DomainEventOutbox> findStuckProcessing(LocalDateTime lockedBefore);
}
