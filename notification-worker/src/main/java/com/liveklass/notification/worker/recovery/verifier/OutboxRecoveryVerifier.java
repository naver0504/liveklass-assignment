package com.liveklass.notification.worker.recovery.verifier;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.domain.DomainEventOutbox;

public interface OutboxRecoveryVerifier {

    ChannelType supports();

    boolean exists(DomainEventOutbox outbox);
}
