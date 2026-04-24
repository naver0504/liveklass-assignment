package com.liveklass.notification.worker.recovery.verifier;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.application.service.InAppNotificationService;
import com.liveklass.notification.domain.DomainEventOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InAppOutboxRecoveryVerifier implements OutboxRecoveryVerifier {

    private final InAppNotificationService inAppNotificationService;

    @Override
    public ChannelType supports() {
        return ChannelType.IN_APP;
    }

    @Override
    public boolean exists(final DomainEventOutbox outbox) {
        return inAppNotificationService.existsByOutboxId(outbox.id().id());
    }
}
