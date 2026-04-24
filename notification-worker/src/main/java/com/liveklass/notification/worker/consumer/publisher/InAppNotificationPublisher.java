package com.liveklass.notification.worker.consumer.publisher;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.PayloadValidator;
import com.liveklass.notification.worker.consumer.NotificationPublisher;
import com.liveklass.notification.worker.consumer.PublishResult;
import com.liveklass.notification.application.service.InAppNotificationService;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.InAppNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InAppNotificationPublisher implements NotificationPublisher {

    private final InAppNotificationService inAppNotificationService;
    private final ObjectMapper objectMapper;

    @Override
    public ChannelType supports() {
        return ChannelType.IN_APP;
    }

    @Override
    public PublishResult publish(final DomainEventOutbox outbox) {
        try {
            final JsonNode payload = objectMapper.readTree(outbox.payload());
            final String title = payload.path("title").asText();
            final String body  = payload.path("body").asText();

            if (title.isBlank() || body.isBlank()) {
                return new PublishResult.PermanentFailure("IN_APP payload missing title or body");
            }

            inAppNotificationService.createInAppNotification(
                    outbox.id().id(), outbox.recipientId(), title, body,
                    outbox.retryState().nextAttemptAt(), LocalDateTime.now());

            log.info("[InAppNotification] sent outboxId={} recipientId={}", outbox.id().id(), outbox.recipientId());
            return new PublishResult.Success();

        } catch (final JacksonException e) {
            return new PublishResult.PermanentFailure("payload parse error: " + e.getMessage());
        } catch (final Exception e) {
            return new PublishResult.RetryableFailure(e.getMessage());
        }
    }

    @Override
    public List<PublishResult> publishBatch(final List<DomainEventOutbox> outboxes) {
        final List<Parsed> parsed = outboxes.stream().map(this::parse).toList();

        final List<InAppNotification> toCreate = parsed.stream()
                .filter(Parsed.Valid.class::isInstance)
                .map(p -> ((Parsed.Valid) p).notification())
                .toList();

        final PublishResult batchOutcome = toCreate.isEmpty()
                ? new PublishResult.Success()
                : batchInsert(toCreate);

        return parsed.stream().map(p -> p.toResult(batchOutcome)).toList();
    }

    private Parsed parse(final DomainEventOutbox outbox) {
        try {
            final JsonNode payload = objectMapper.readTree(outbox.payload());
            final String title = payload.path("title").asText();
            final String body  = payload.path("body").asText();
            final String publishedAtValue = payload.path("metadata").path("publishedAt").asText();
            PayloadValidator.validateNotBlank("title", title);
            PayloadValidator.validateNotBlank("body", body);
            final LocalDateTime publishedAt = publishedAtValue.isBlank()
                    ? LocalDateTime.now()
                    : LocalDateTime.parse(publishedAtValue);
            return new Parsed.Valid(InAppNotification.create(
                    outbox.id().id(), outbox.recipientId(), title, body,
                    publishedAt, LocalDateTime.now()));
        } catch (final JacksonException | RuntimeException e) {
            return new Parsed.Invalid(new PublishResult.PermanentFailure("payload parse error: " + e.getMessage()));
        }
    }

    private PublishResult batchInsert(final List<InAppNotification> notifications) {
        try {
            inAppNotificationService.createInAppNotificationsBatch(notifications);
            log.info("[InAppNotification] batch sent {} notifications", notifications.size());
            return new PublishResult.Success();
        } catch (final Exception e) {
            log.warn("[InAppNotification] batch failed: {}", e.getMessage());
            return new PublishResult.RetryableFailure(e.getMessage());
        }
    }

    private sealed interface Parsed permits Parsed.Valid, Parsed.Invalid {
        PublishResult toResult(PublishResult batchOutcome);

        record Valid(InAppNotification notification) implements Parsed {
            @Override
            public PublishResult toResult(final PublishResult batchOutcome) {
                return batchOutcome;
            }
        }

        record Invalid(PublishResult.PermanentFailure failure) implements Parsed {
            @Override
            public PublishResult toResult(final PublishResult ignored) {
                return failure;
            }
        }
    }
}
