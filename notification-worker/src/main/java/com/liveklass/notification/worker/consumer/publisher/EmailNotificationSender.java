package com.liveklass.notification.worker.consumer.publisher;

public interface EmailNotificationSender {

    void send(String recipientEmail, String subject, String body, String idempotencyKey);

    boolean isSent(String idempotencyKey);
}
