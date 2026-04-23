package com.liveklass.notification.domain;

import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.exception.OutboxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

@Tag("UNIT_TEST")
@DisplayName("OutboxStatus 전이 규칙은")
class OutboxStatusTest {

    @Nested
    @DisplayName("유효한 전이는")
    class Describe_valid_transitions {

        @Test
        @DisplayName("PENDING → PROCESSING을 허용한다")
        void it_allows_pending_to_processing() {
            assertThatCode(() -> OutboxStatus.PENDING.validateTransitionTo(OutboxStatus.PROCESSING))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("PROCESSING → SENT를 허용한다")
        void it_allows_processing_to_sent() {
            assertThatCode(() -> OutboxStatus.PROCESSING.validateTransitionTo(OutboxStatus.SENT))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("PROCESSING → PENDING을 허용한다 (재시도)")
        void it_allows_processing_to_pending() {
            assertThatCode(() -> OutboxStatus.PROCESSING.validateTransitionTo(OutboxStatus.PENDING))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("PROCESSING → DEAD_LETTER를 허용한다")
        void it_allows_processing_to_dead_letter() {
            assertThatCode(() -> OutboxStatus.PROCESSING.validateTransitionTo(OutboxStatus.DEAD_LETTER))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("DEAD_LETTER → PENDING을 허용한다 (수동 재시도)")
        void it_allows_dead_letter_to_pending() {
            assertThatCode(() -> OutboxStatus.DEAD_LETTER.validateTransitionTo(OutboxStatus.PENDING))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("유효하지 않은 전이는")
    class Describe_invalid_transitions {

        @Test
        @DisplayName("SENT → 어떤 상태로도 전이하면 INVALID_STATUS_TRANSITION 예외를 던진다")
        void it_throws_when_transitioning_from_sent() {
            for (final OutboxStatus next : OutboxStatus.values()) {
                ExceptionAssertions.assertThatExceptionOfType(
                        () -> OutboxStatus.SENT.validateTransitionTo(next),
                        OutboxException.INVALID_STATUS_TRANSITION
                );
            }
        }

        @Test
        @DisplayName("PENDING → SENT 직접 전이는 INVALID_STATUS_TRANSITION 예외를 던진다")
        void it_throws_when_pending_to_sent() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> OutboxStatus.PENDING.validateTransitionTo(OutboxStatus.SENT),
                    OutboxException.INVALID_STATUS_TRANSITION
            );
        }

        @Test
        @DisplayName("PENDING → DEAD_LETTER 직접 전이는 INVALID_STATUS_TRANSITION 예외를 던진다")
        void it_throws_when_pending_to_dead_letter() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> OutboxStatus.PENDING.validateTransitionTo(OutboxStatus.DEAD_LETTER),
                    OutboxException.INVALID_STATUS_TRANSITION
            );
        }

        @Test
        @DisplayName("DEAD_LETTER → SENT 직접 전이는 INVALID_STATUS_TRANSITION 예외를 던진다")
        void it_throws_when_dead_letter_to_sent() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> OutboxStatus.DEAD_LETTER.validateTransitionTo(OutboxStatus.SENT),
                    OutboxException.INVALID_STATUS_TRANSITION
            );
        }
    }
}
