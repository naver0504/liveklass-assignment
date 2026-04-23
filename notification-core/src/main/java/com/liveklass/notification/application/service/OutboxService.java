package com.liveklass.notification.application.service;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.application.repository.OutboxRepository;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.exception.OutboxException;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.domain.vo.IdempotencyKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Transactional
    public DomainEventOutbox createOutbox(
            final String idempotencyKey,
            final Long requesterId,
            final Long recipientId,
            final Topic topic,
            final ChannelType channelType,
            final String referenceId,
            final String payload,
            final LocalDateTime nextAttemptAt,
            final int maxAttempts
    ) {
        return outboxRepository.save(
                DomainEventOutbox.create(
                        new IdempotencyKey(idempotencyKey),
                        requesterId,
                        recipientId,
                        new EventRef(topic, channelType, referenceId),
                        payload,
                        nextAttemptAt,
                        maxAttempts
                )
        );
    }

    @Transactional
    public List<DomainEventOutbox> claimAll(final List<DomainEventOutbox> outboxes, final LocalDateTime lockedAt) {
        final List<DomainEventOutbox> claimed = outboxes.stream()
                .map(o -> o.claim(lockedAt))
                .toList();
        return outboxRepository.saveAll(claimed);
    }

    @Transactional
    public List<DomainEventOutbox> saveAll(final List<DomainEventOutbox> outboxes) {
        return outboxRepository.saveAll(outboxes);
    }

    @Transactional(readOnly = true)
    public DomainEventOutbox findRequestNotificationById(final Long outboxId, final Long requesterId) {
        final DomainEventOutbox outbox = outboxRepository.findById(new OutboxId(outboxId))
                .orElseThrow(() -> ExceptionCreator.create(OutboxException.OUTBOX_NOT_FOUND, "outboxId: " + outboxId));
        final Topic topic = outbox.eventRef().topic();
        if (topic != Topic.IN_APP_NOTIFICATION_REQUEST && topic != Topic.EMAIL_NOTIFICATION_REQUEST) {
            throw ExceptionCreator.create(OutboxException.OUTBOX_NOT_FOUND, "outboxId: " + outboxId);
        }
        outbox.validateRequester(requesterId);
        return outbox;
    }

    @Transactional(readOnly = true)
    public List<DomainEventOutbox> findPendingOutboxes(final LocalDateTime scheduledAt) {
        return outboxRepository.findTop50PendingBefore(scheduledAt);
    }

    @Transactional(readOnly = true)
    public List<DomainEventOutbox> findStuckOutboxes(final LocalDateTime lockedBefore) {
        return outboxRepository.findStuckProcessing(lockedBefore);
    }
}
