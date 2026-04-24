package com.liveklass.notification.worker.recovery.verifier;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.worker.consumer.publisher.EmailNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOutboxRecoveryVerifier implements OutboxRecoveryVerifier {

    private final EmailNotificationSender emailNotificationSender;

    @Override
    public ChannelType supports() {
        return ChannelType.EMAIL;
    }

    @Override
    public boolean exists(final DomainEventOutbox outbox) {
        return emailNotificationSender.isSent(outbox.idempotencyKey());
    }
}
