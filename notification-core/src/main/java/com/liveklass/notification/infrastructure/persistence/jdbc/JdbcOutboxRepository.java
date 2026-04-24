package com.liveklass.notification.infrastructure.persistence.jdbc;

import com.liveklass.notification.domain.DomainEventOutbox;

import java.time.LocalDateTime;
import java.util.List;

public interface JdbcOutboxRepository {

    void upsert(DomainEventOutbox outbox);

    void batchUpdate(List<DomainEventOutbox> outboxes);

    void batchUpdateProcessingResults(List<DomainEventOutbox> outboxes);

    List<DomainEventOutbox> findPendingBefore(LocalDateTime scheduledAt, int limit);
}
