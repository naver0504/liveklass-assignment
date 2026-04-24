package com.liveklass.notification.worker.consumer.publisher;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.PayloadValidator;
import com.liveklass.notification.worker.consumer.NotificationPublisher;
import com.liveklass.notification.worker.consumer.PublishResult;
import com.liveklass.notification.domain.DomainEventOutbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class EmailNotificationPublisher implements NotificationPublisher {

    private final ObjectMapper objectMapper;
    private final Executor emailPublisherExecutor;
    private final EmailNotificationSender emailNotificationSender;

    public EmailNotificationPublisher(
            final ObjectMapper objectMapper,
            @Qualifier("emailPublisherExecutor") final Executor emailPublisherExecutor,
            final EmailNotificationSender emailNotificationSender
    ) {
        this.objectMapper = objectMapper;
        this.emailPublisherExecutor = emailPublisherExecutor;
        this.emailNotificationSender = emailNotificationSender;
    }

    @Override
    public ChannelType supports() {
        return ChannelType.EMAIL;
    }

    @Override
    public PublishResult publish(final DomainEventOutbox outbox) {
        try {
            final JsonNode payload = objectMapper.readTree(outbox.payload());
            final String subject        = payload.path("subject").asText();
            final JsonNode bodyNode     = payload.path("body");
            final String recipientEmail = payload.path("metadata").path("recipientEmail").asText();
            PayloadValidator.validateNotBlank("subject", subject);
            PayloadValidator.validateBodyNode("body", bodyNode);
            PayloadValidator.validateNotBlank("recipientEmail", recipientEmail);

            final String body = bodyNode.isTextual() ? bodyNode.asText() : bodyNode.toString();
            emailNotificationSender.send(
                    recipientEmail,
                    subject,
                    body,
                    outbox.idempotencyKey()
            );
            return new PublishResult.Success();

        } catch (final JacksonException e) {
            return new PublishResult.PermanentFailure("payload parse error: " + e.getMessage());
        } catch (final IllegalArgumentException e) {
            return new PublishResult.PermanentFailure(e.getMessage());
        } catch (final Exception e) {
            return new PublishResult.RetryableFailure(e.getMessage());
        }
    }

    @Override
    public List<PublishResult> publishBatch(final List<DomainEventOutbox> outboxes) {
        final List<CompletableFuture<PublishResult>> futures = outboxes.stream()
                .map(outbox -> CompletableFuture.supplyAsync(() -> publish(outbox), emailPublisherExecutor))
                .toList();
        return futures.stream().map(CompletableFuture::join).toList();
    }
}
