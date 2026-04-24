package com.liveklass.notification.infrastructure.repository;

import com.liveklass.notification.application.repository.OutboxRepository;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.infrastructure.mapper.OutboxEntityMapper;
import com.liveklass.notification.infrastructure.persistence.jdbc.JdbcOutboxRepository;
import com.liveklass.notification.infrastructure.persistence.jpa.JpaOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final JpaOutboxRepository jpaOutboxRepository;
    private final JdbcOutboxRepository jdbcOutboxRepository;
    private final OutboxEntityMapper outboxEntityMapper;

    @Override
    public void save(final DomainEventOutbox outbox) {
        jdbcOutboxRepository.upsert(outbox);
    }

    @Override
    public List<DomainEventOutbox> saveAll(final List<DomainEventOutbox> outboxes) {
        jdbcOutboxRepository.batchUpdate(outboxes);
        return outboxes;
    }

    @Override
    public List<DomainEventOutbox> saveAllProcessingResults(final List<DomainEventOutbox> outboxes) {
        jdbcOutboxRepository.batchUpdateProcessingResults(outboxes);
        return outboxes;
    }

    @Override
    public Optional<DomainEventOutbox> findById(final OutboxId id) {
        return jpaOutboxRepository.findById(id.id())
                .map(outboxEntityMapper::toDomain);
    }

    @Override
    public List<DomainEventOutbox> findPendingBefore(final LocalDateTime scheduledAt, final int limit) {
        return jdbcOutboxRepository.findPendingBefore(scheduledAt, limit);
    }

    @Override
    public List<DomainEventOutbox> findStuckProcessing(final LocalDateTime lockedBefore) {
        return jpaOutboxRepository.findByStatusAndLockedAtBefore(OutboxStatus.PROCESSING, lockedBefore)
                .stream()
                .map(outboxEntityMapper::toDomain)
                .toList();
    }
}
