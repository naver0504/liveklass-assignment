package com.liveklass.notification.worker.recovery;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.worker.config.WorkerProperties;
import com.liveklass.notification.worker.recovery.verifier.OutboxRecoveryVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StuckOutboxRecoveryScheduler {

    private static final long RECOVERY_RETRY_DELAY_MINUTES = 3L;

    private final Map<ChannelType, OutboxRecoveryVerifier> verifierRegistry;
    private final OutboxService outboxService;
    private final WorkerProperties workerProperties;

    public StuckOutboxRecoveryScheduler(
            final List<OutboxRecoveryVerifier> verifiers,
            final OutboxService outboxService,
            final WorkerProperties workerProperties
    ) {
        this.verifierRegistry = verifiers.stream()
                .collect(Collectors.toMap(OutboxRecoveryVerifier::supports, Function.identity()));
        this.outboxService = outboxService;
        this.workerProperties = workerProperties;
    }

    @Scheduled(fixedDelay = 60_000)
    public void recover() {
        final long stuckThresholdMinutes = workerProperties.stuckThresholdMinutes();
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime lockedBefore = now.minusMinutes(stuckThresholdMinutes);

        final List<DomainEventOutbox> stuck = outboxService.findStuckOutboxes(lockedBefore);

        if (stuck.isEmpty()) {
            return;
        }

        log.info("[StuckOutboxRecoveryScheduler] found {} stuck outboxes, threshold={}min",
                stuck.size(), stuckThresholdMinutes);

        final List<DomainEventOutbox> recovered = stuck.stream()
                .map(outbox -> releaseOrDeadLetter(outbox, now))
                .toList();

        outboxService.saveAllProcessingResults(recovered);
    }

    private DomainEventOutbox releaseOrDeadLetter(final DomainEventOutbox outbox, final LocalDateTime now) {
        if (isAlreadyProcessed(outbox)) {
            log.info("[StuckOutboxRecoveryScheduler] correcting outboxId={} to SENT", outbox.id().id());
            return outbox.correctToSent();
        }

        if (!outbox.retryState().isRetryable()) {
            log.warn("[StuckOutboxRecoveryScheduler] dead-lettering outboxId={}", outbox.id().id());
            return outbox.markDeadLetter("stuck and exhausted: attempt_count >= max_attempts");
        }
        log.info("[StuckOutboxRecoveryScheduler] releasing outboxId={} to retry", outbox.id().id());
        return outbox.releaseToRetry(now.plusMinutes(RECOVERY_RETRY_DELAY_MINUTES));
    }

    private boolean isAlreadyProcessed(final DomainEventOutbox outbox) {
        final OutboxRecoveryVerifier verifier = verifierRegistry.get(outbox.eventRef().channelType());
        if (verifier == null) {
            log.warn("[StuckOutboxRecoveryScheduler] no verifier registered for channelType={}", outbox.eventRef().channelType());
            return false;
        }
        return verifier.exists(outbox);
    }
}
