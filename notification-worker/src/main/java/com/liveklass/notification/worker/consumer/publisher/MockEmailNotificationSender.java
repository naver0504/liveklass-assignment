package com.liveklass.notification.worker.consumer.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MockEmailNotificationSender implements EmailNotificationSender {

    @Override
    public void send(
            final String recipientEmail,
            final String subject,
            final String body,
            final String idempotencyKey
    ) {
        log.info(
                "[EmailNotification] mock sent to={} subject={} idempotencyKey={} body={}",
                recipientEmail,
                subject,
                idempotencyKey,
                body
        );
    }

    @Override
    public boolean isSent(final String idempotencyKey) {
        final boolean sent = ThreadLocalRandom.current().nextInt(10) < 9;
        log.debug("[EmailNotification] mock verify idempotencyKey={} sent={}", idempotencyKey, sent);
        return sent;
    }
}
