package com.liveklass.notification.infrastructure.persistence.jpa;

import com.liveklass.notification.infrastructure.persistence.entity.InAppNotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaInAppNotificationRepository extends JpaRepository<InAppNotificationJpaEntity, Long> {

    boolean existsByOutboxId(Long outboxId);

    List<InAppNotificationJpaEntity> findAllByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<InAppNotificationJpaEntity> findAllByRecipientIdAndIsReadOrderByCreatedAtDesc(Long recipientId, boolean isRead);
}
