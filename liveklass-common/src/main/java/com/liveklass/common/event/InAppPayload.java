package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * IN_APP 채널 payload 빌더.
 *
 * <p>{@code title}과 {@code body}는 필수다. 빌더 생성 시 전달하므로 누락이 컴파일 시점에 드러난다.
 * 추가 컨텍스트는 {@link Builder#metadata} 체이닝으로 확장한다.
 *
 * <pre>{@code
 * JsonNode payload = InAppPayload.builder("수강 신청 완료", lectureTitle + " 수강 신청이 완료되었습니다.")
 *     .metadata("screen", "LECTURE_DETAIL")
 *     .metadata("lectureId", lectureId)
 *     .build();
 * }</pre>
 */
public final class InAppPayload {

    private InAppPayload() {}

    public static Builder builder(final String title, final String body) {
        return new Builder(title, body);
    }

    public static final class Builder extends PayloadBuilder<Builder> {

        private final String title;
        private final String body;

        private Builder(final String title, final String body) {
            this.title = Objects.requireNonNull(title, "title must not be null");
            this.body  = Objects.requireNonNull(body,  "body must not be null");
        }

        /** IN_APP 최소 계약(title, body, metadata)을 만족하는 JsonNode를 반환한다. */
        @Override
        public JsonNode build() {
            final ObjectNode root = JsonNodeFactory.instance.objectNode();
            root.put("title", title);
            root.put("body",  body);
            root.set("metadata", metadata);
            return root;
        }
    }
}
