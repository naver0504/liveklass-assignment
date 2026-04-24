package com.liveklass.notification.worker.consumer.dispatcher;

import com.liveklass.common.event.ChannelType;
import com.liveklass.notification.worker.consumer.NotificationPublisher;
import com.liveklass.notification.worker.consumer.PublishResult;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.worker.consumer.policy.BackoffPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OutboxDispatcher {

    private final Map<ChannelType, NotificationPublisher> publisherRegistry;
    private final BackoffPolicy backoffPolicy;

    public OutboxDispatcher(
            final List<NotificationPublisher> publishers,
            final BackoffPolicy backoffPolicy
    ) {
        this.publisherRegistry = publishers.stream()
                .collect(Collectors.toMap(NotificationPublisher::supports, Function.identity()));
        this.backoffPolicy = backoffPolicy;
    }

    public List<DomainEventOutbox> dispatchAll(
            final List<DomainEventOutbox> outboxes,
            final LocalDateTime now
    ) {
        final Map<ChannelType, List<DomainEventOutbox>> grouped = outboxes.stream()
                .collect(Collectors.groupingBy(o -> o.eventRef().channelType()));

        final List<DomainEventOutbox> results = new ArrayList<>(outboxes.size());

        grouped.forEach((channelType, channelOutboxes) -> {
            final NotificationPublisher publisher = publisherRegistry.get(channelType);
            if (publisher == null) {
                log.error("[OutboxDispatcher] no publisher registered for channelType={}", channelType);
                final PublishResult.PermanentFailure noPublisher = new PublishResult.PermanentFailure("no publisher registered for channelType: " + channelType);
                channelOutboxes.stream()
                        .map(o -> applyResult(o, noPublisher, now))
                        .forEach(results::add);
                return;
            }

            final List<PublishResult> publishResults = publisher.publishBatch(channelOutboxes);
            for (int i = 0; i < channelOutboxes.size(); i++) {
                results.add(applyResult(channelOutboxes.get(i), publishResults.get(i), now));
            }
        });

        return results;
    }

    private DomainEventOutbox applyResult(
            final DomainEventOutbox outbox,
            final PublishResult result,
            final LocalDateTime now
    ) {
        return switch (result) {
            case PublishResult.Success success -> {
                log.info("[OutboxDispatcher] success outboxId={}", outbox.id().id());
                yield outbox.complete();
            }
            case PublishResult.PermanentFailure permanentFailure -> {
                log.warn("[OutboxDispatcher] permanent failure outboxId={} error={}", outbox.id().id(), permanentFailure.errorMessage());
                yield outbox.permanentFail(permanentFailure.errorMessage());
            }
            case PublishResult.RetryableFailure retryableFailure -> {
                log.warn("[OutboxDispatcher] retryable failure outboxId={} error={}", outbox.id().id(), retryableFailure.errorMessage());
                yield outbox.fail(retryableFailure.errorMessage(),
                        backoffPolicy.nextAttemptAt(outbox.eventRef().channelType(), outbox.retryState().attemptCount() + 1, now));
            }
        };
    }
}
