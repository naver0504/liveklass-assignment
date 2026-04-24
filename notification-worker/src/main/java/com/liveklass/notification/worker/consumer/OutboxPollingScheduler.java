package com.liveklass.notification.worker.consumer;

import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.worker.config.WorkerProperties;
import com.liveklass.notification.worker.consumer.dispatcher.OutboxDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollingScheduler {

    private final OutboxService outboxService;
    private final OutboxDispatcher outboxDispatcher;
    private final WorkerProperties workerProperties;

    @Scheduled(fixedDelayString = "${notification.polling.fixed-delay-ms:1000}")
    public void poll() {
        if (!workerProperties.polling().enabled()) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();
        final List<DomainEventOutbox> claimed = outboxService.claimPendingOutboxes(
                now, workerProperties.polling().batchSize());
        if (claimed.isEmpty()) {
            return;
        }

        log.debug("[OutboxPollingScheduler] claimed {} outboxes", claimed.size());

        final List<DomainEventOutbox> dispatched = outboxDispatcher.dispatchAll(claimed, now);
        outboxService.saveAllProcessingResults(dispatched);

        log.debug("[OutboxPollingScheduler] processed {} outboxes", dispatched.size());
    }
}
