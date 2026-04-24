package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public final class PayloadValidator {

    private PayloadValidator() {}

    public static void validateTopicChannel(final Topic topic, final ChannelType expectedChannelType) {
        Objects.requireNonNull(topic, "topic must not be null");
        Objects.requireNonNull(expectedChannelType, "expectedChannelType must not be null");
        if (topic.getChannelType() != expectedChannelType) {
            throw new IllegalArgumentException("topic must support " + expectedChannelType + " channel");
        }
    }

    public static void validateNotBlank(final String fieldName, final String value) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    public static void validateBodyNode(final String fieldName, final JsonNode bodyNode) {
        Objects.requireNonNull(bodyNode, fieldName + " must not be null");
        if (bodyNode.isMissingNode() || bodyNode.isNull()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        if (bodyNode.isTextual() && bodyNode.asText().isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    public static void validate(final ChannelType channelType, final JsonNode payload) {
        Objects.requireNonNull(channelType, "channelType must not be null");
        Objects.requireNonNull(payload, "payload must not be null");

        switch (channelType) {
            case IN_APP -> {
                validateNotBlank("title", payload.path("title").asText());
                validateNotBlank("body", payload.path("body").asText());
            }
            case EMAIL -> {
                validateNotBlank("subject", payload.path("subject").asText());
                validateBodyNode("body", payload.path("body"));
                validateNotBlank("recipientEmail", payload.path("metadata").path("recipientEmail").asText());
            }
        }
    }
}
