package com.liveklass.notification.infrastructure.persistence.jpa;

import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.infrastructure.persistence.entity.OutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaOutboxRepository extends JpaRepository<OutboxJpaEntity, Long> {

    List<OutboxJpaEntity> findByStatusAndLockedAtBefore(OutboxStatus status, LocalDateTime lockedAt);
}
