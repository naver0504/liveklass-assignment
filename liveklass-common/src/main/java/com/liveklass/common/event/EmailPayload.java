package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Objects;

/**
 * EMAIL 채널 payload 빌더.
 *
 * <p>{@code subject}, {@code body}, {@code recipientEmail}은 필수다.
 * 빌더 생성 시 전달하므로 누락이 컴파일 시점에 드러난다.
 *
 * <p>{@code body}는 {@link JsonNode}로 받아 표현 방식을 caller가 결정한다.
 * 문자열 편의 오버로드를 통해 기존 방식도 유지된다.
 *
 * <pre>{@code
 * // 텍스트 본문
 * EmailPayload.builder("제목", "본문 텍스트", recipientEmail).build();
 *
 * // HTML 본문
 * EmailPayload.builder("제목", TextNode.valueOf("<h1>HTML</h1>"), recipientEmail)
 *     .metadata("bodyType", "HTML")
 *     .build();
 *
 * // 구조화된 본문 (카드 타입)
 * ObjectNode card = JsonNodeFactory.instance.objectNode()
 *     .put("headline", "결제가 완료됐습니다")
 *     .put("cta", "영수증 보기");
 * EmailPayload.builder("제목", card, recipientEmail)
 *     .metadata("bodyType", "CARD")
 *     .build();
 * }</pre>
 */
public final class EmailPayload {

    private EmailPayload() {}

    /** 텍스트 본문 편의 오버로드. body를 TextNode로 변환한다. */
    public static Builder builder(final String subject, final String body, final String recipientEmail) {
        return new Builder(subject, TextNode.valueOf(body), recipientEmail);
    }

    /** body 표현 방식을 caller가 직접 결정하는 오버로드. 텍스트/HTML/구조화 본문 모두 가능. */
    public static Builder builder(final String subject, final JsonNode body, final String recipientEmail) {
        return new Builder(subject, body, recipientEmail);
    }

    public static final class Builder extends PayloadBuilder<Builder> {

        private final String subject;
        private final JsonNode body;

        private Builder(final String subject, final JsonNode body, final String recipientEmail) {
            this.subject = Objects.requireNonNull(subject,        "subject must not be null");
            this.body    = Objects.requireNonNull(body,           "body must not be null");
            Objects.requireNonNull(recipientEmail, "recipientEmail must not be null");
            metadata.put("recipientEmail", recipientEmail);
        }

        /** EMAIL 최소 계약(subject, body, metadata.recipientEmail)을 만족하는 JsonNode를 반환한다. */
        @Override
        public JsonNode build() {
            final ObjectNode root = JsonNodeFactory.instance.objectNode();
            root.put("subject", subject);
            root.set("body",    body);
            root.set("metadata", metadata);
            return root;
        }
    }
}
