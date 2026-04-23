package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 채널별 payload 빌더의 공통 추상 클래스.
 *
 * <p>새 채널 추가 시 이 클래스를 상속하고, 채널 고유의 필수 필드만 정의하면 된다.
 * {@link #metadata} 체이닝은 모든 채널 빌더에서 공통으로 사용할 수 있다.
 *
 * <pre>{@code
 * // 새 채널 추가 예시
 * public final class SmsPayload {
 *     public static Builder builder(String message, String phoneNumber) {
 *         return new Builder(message, phoneNumber);
 *     }
 *
 *     public static final class Builder extends PayloadBuilder<Builder> {
 *         // 채널 고유 필수 필드만 추가
 *     }
 * }
 * }</pre>
 *
 * @param <T> 구체 빌더 타입 (메서드 체이닝 반환 타입에 사용)
 */
public abstract class PayloadBuilder<T extends PayloadBuilder<T>> {

    protected final ObjectNode metadata = JsonNodeFactory.instance.objectNode();

    /** metadata에 문자열 값을 추가한다. */
    @SuppressWarnings("unchecked")
    public final T metadata(final String key, final String value) {
        metadata.put(key, value);
        return (T) this;
    }

    /** metadata에 숫자 값을 추가한다. */
    @SuppressWarnings("unchecked")
    public final T metadata(final String key, final long value) {
        metadata.put(key, value);
        return (T) this;
    }

    /** metadata에 불리언 값을 추가한다. */
    @SuppressWarnings("unchecked")
    public final T metadata(final String key, final boolean value) {
        metadata.put(key, value);
        return (T) this;
    }

    /** 채널별 최소 계약을 만족하는 {@link JsonNode}를 반환한다. */
    public abstract JsonNode build();
}
