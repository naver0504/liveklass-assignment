package com.liveklass.notification.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@Tag("UNIT_TEST")
@DisplayName("SentResult는")
class SentResultTest {

    private static final LocalDateTime SENT_AT = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Nested
    @DisplayName("of()는")
    class Describe_of {

        @Test
        @DisplayName("sentAt과 providerMessageId를 그대로 보유한다")
        void it_holds_sent_at_and_provider_message_id() {
            // given
            final String providerMessageId = "msg-abc123";

            // when
            final SentResult result = SentResult.of(SENT_AT, providerMessageId);

            // then
            assertThat(result.sentAt()).isEqualTo(SENT_AT);
            assertThat(result.providerMessageId()).isEqualTo(providerMessageId);
        }

        @Test
        @DisplayName("providerMessageId는 null을 허용한다 (provider가 ID를 반환하지 않는 경우)")
        void it_allows_null_provider_message_id() {
            // given & when
            final SentResult result = SentResult.of(SENT_AT, null);

            // then
            assertThat(result.providerMessageId()).isNull();
        }

        @Test
        @DisplayName("sentAt이 null이면 NPE를 던진다")
        void it_throws_when_sent_at_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> SentResult.of(null, "msg-001"))
                    .withMessageContaining("sentAt");
        }
    }
}
