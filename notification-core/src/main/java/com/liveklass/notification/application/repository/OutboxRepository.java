package com.liveklass.notification.application.repository;

import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.id.OutboxId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    void save(DomainEventOutbox outbox);

    List<DomainEventOutbox> saveAll(List<DomainEventOutbox> outboxes);

    List<DomainEventOutbox> saveAllProcessingResults(List<DomainEventOutbox> outboxes);

    Optional<DomainEventOutbox> findById(OutboxId id);

    List<DomainEventOutbox> findPendingBefore(LocalDateTime scheduledAt, int limit);

    List<DomainEventOutbox> findStuckProcessing(LocalDateTime lockedBefore);
}
