package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.EmailPayload;
import com.liveklass.common.event.Topic;

import java.time.LocalDateTime;
import java.util.Objects;

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
        Objects.requireNonNull(paymentId, "paymentId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(enrollmentId, "enrollmentId must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(recipientEmail, "recipientEmail must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        Objects.requireNonNull(publishedAt, "publishedAt must not be null");

        if (paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId must not be blank");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
        if (recipientEmail.isBlank()) {
            throw new IllegalArgumentException("recipientEmail must not be blank");
        }
        if (referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId must not be blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
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
}
