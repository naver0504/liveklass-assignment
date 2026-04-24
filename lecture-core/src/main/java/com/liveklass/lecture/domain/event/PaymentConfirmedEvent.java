package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.EmailPayload;
import com.liveklass.common.event.Topic;
import com.liveklass.lecture.domain.exception.EnrollmentException;

import java.time.LocalDateTime;

public record PaymentConfirmedEvent(
        String paymentId,
        Long userId,
        Long enrollmentId,
        long amount,
        String currency,
        String recipientEmail,
        Long recipientId,
        String referenceId,
        LocalDateTime publishedAt
) implements DomainEvent {
    public PaymentConfirmedEvent {
        requireText(paymentId, "paymentId");
        requireValue(userId, "userId");
        requireValue(enrollmentId, "enrollmentId");
        requireText(currency, "currency");
        requireText(recipientEmail, "recipientEmail");
        requireValue(recipientId, "recipientId");
        requireText(referenceId, "referenceId");
        requireValue(publishedAt, "publishedAt");
        if (amount < 0) {
            throw ExceptionCreator.create(EnrollmentException.PAYMENT_EVENT_AMOUNT_INVALID, "field: amount");
        }
    }

    @Override
    public Topic topic() {
        return Topic.PAYMENT_CONFIRMED;
    }

    @Override
    public JsonNode payload() {
        final ObjectNode body = JsonNodeFactory.instance.objectNode();
        body.put("headline", "결제가 완료되었습니다");
        body.put("message", amount + " " + currency + " 결제가 승인되었습니다.");

        return EmailPayload.builder("결제가 완료되었습니다", body, recipientEmail)
                .metadata("paymentId", paymentId)
                .metadata("amount", amount)
                .metadata("currency", currency)
                .metadata("confirmedAt", publishedAt.toString())
                .build();
    }

    private static void requireValue(final Object value, final String fieldName) {
        if (value == null) {
            throw ExceptionCreator.create(EnrollmentException.PAYMENT_EVENT_REQUIRED, "field: " + fieldName);
        }
    }

    private static void requireText(final String value, final String fieldName) {
        if (value == null) {
            throw ExceptionCreator.create(EnrollmentException.PAYMENT_EVENT_REQUIRED, "field: " + fieldName);
        }
        if (value.isBlank()) {
            throw ExceptionCreator.create(EnrollmentException.PAYMENT_EVENT_BLANK, "field: " + fieldName);
        }
    }
}
