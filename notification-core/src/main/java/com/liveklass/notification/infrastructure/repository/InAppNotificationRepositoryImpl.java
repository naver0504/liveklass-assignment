package com.liveklass.notification.infrastructure.repository;

import com.liveklass.notification.application.repository.InAppNotificationRepository;
import com.liveklass.notification.domain.InAppNotification;
import com.liveklass.notification.domain.id.NotificationId;
import com.liveklass.notification.infrastructure.mapper.InAppNotificationEntityMapper;
import com.liveklass.notification.infrastructure.persistence.jdbc.JdbcInAppNotificationRepository;
import com.liveklass.notification.infrastructure.persistence.jpa.JpaInAppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InAppNotificationRepositoryImpl implements InAppNotificationRepository {

    private final JpaInAppNotificationRepository jpaInAppNotificationRepository;
    private final JdbcInAppNotificationRepository jdbcInAppNotificationRepository;
    private final InAppNotificationEntityMapper inAppNotificationEntityMapper;

    @Override
    public void saveAll(final List<InAppNotification> notifications) {
        jdbcInAppNotificationRepository.batchInsert(notifications);
    }

    @Override
    public void save(final InAppNotification notification) {
        jdbcInAppNotificationRepository.upsert(notification);
    }

    @Override
    public Optional<InAppNotification> findById(final NotificationId id) {
        return jpaInAppNotificationRepository.findById(id.id())
                .map(inAppNotificationEntityMapper::toDomain);
    }

    @Override
    public boolean existsByOutboxId(final Long outboxId) {
        return jpaInAppNotificationRepository.existsByOutboxId(outboxId);
    }

    @Override
    public List<InAppNotification> findAllByRecipientId(final Long recipientId) {
        return jpaInAppNotificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(inAppNotificationEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<InAppNotification> findAllByRecipientIdAndIsRead(final Long recipientId, final boolean isRead) {
        return jpaInAppNotificationRepository.findAllByRecipientIdAndIsReadOrderByCreatedAtDesc(recipientId, isRead)
                .stream()
                .map(inAppNotificationEntityMapper::toDomain)
                .toList();
    }
}
