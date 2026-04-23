package com.liveklass.notification.domain.vo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 발송 완료 결과. SENT 상태일 때만 non-null이다.
 * providerMessageId는 provider가 발송 성공 후 반환하는 ID다.
 * stuck recovery에서 "실제 발송 여부" 확인 및 멱등성 키 조회에 활용한다.
 */
public record SentResult(
        LocalDateTime sentAt,
        String providerMessageId
) {
    public SentResult {
        Objects.requireNonNull(sentAt, "sentAt must not be null");
    }

    public static SentResult of(final LocalDateTime sentAt, final String providerMessageId) {
        return new SentResult(sentAt, providerMessageId);
    }
}
