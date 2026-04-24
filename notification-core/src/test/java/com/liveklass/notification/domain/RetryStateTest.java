package com.liveklass.notification.domain;

import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.notification.domain.exception.OutboxException;
import com.liveklass.notification.domain.vo.RetryState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@Tag("UNIT_TEST")
@DisplayName("RetryState는")
class RetryStateTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Nested
    @DisplayName("initial()은")
    class Describe_initial {

        @Test
        @DisplayName("attemptCount=0, lastError=null로 초기화된다")
        void it_initializes_with_zero_attempt_and_no_error() {
            // given
            final int maxAttempts = 3;

            // when
            final RetryState state = RetryState.initial(maxAttempts, NOW);

            // then
            assertThat(state.attemptCount()).isZero();
            assertThat(state.maxAttempts()).isEqualTo(maxAttempts);
            assertThat(state.nextAttemptAt()).isEqualTo(NOW);
            assertThat(state.lastError()).isNull();
        }

        @Test
        @DisplayName("maxAttempts가 0 이하면 INVALID_RETRY_STATE 예외를 던진다")
        void it_throws_when_max_attempts_is_zero_or_less() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> RetryState.initial(0, NOW),
                    OutboxException.INVALID_RETRY_STATE
            );
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> RetryState.initial(-1, NOW),
                    OutboxException.INVALID_RETRY_STATE
            );
        }

        @Test
        @DisplayName("nextAttemptAt이 null이면 NPE를 던진다")
        void it_throws_when_next_attempt_at_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> RetryState.initial(3, null))
                    .withMessageContaining("nextAttemptAt");
        }
    }

    @Nested
    @DisplayName("isRetryable()은")
    class Describe_is_retryable {

        @Test
        @DisplayName("attemptCount < maxAttempts이면 true를 반환한다")
        void it_returns_true_when_attempt_count_less_than_max() {
            // given
            final RetryState state = RetryState.initial(3, NOW);

            // when & then
            assertThat(state.isRetryable()).isTrue();
        }

        @Test
        @DisplayName("attemptCount == maxAttempts이면 false를 반환한다")
        void it_returns_false_when_attempt_count_equals_max() {
            // given
            final RetryState state = new RetryState(3, 3, NOW, null);

            // when & then
            assertThat(state.isRetryable()).isFalse();
        }
    }

    @Nested
    @DisplayName("increment()은")
    class Describe_increment {

        @Test
        @DisplayName("attemptCount를 1 증가시키고 nextAttemptAt을 갱신한다")
        void it_increments_attempt_count_and_updates_next_attempt_at() {
            // given
            final RetryState state = RetryState.initial(3, NOW);
            final LocalDateTime nextAttemptAt = NOW.plusMinutes(5);

            // when
            final RetryState incremented = state.increment(nextAttemptAt);

            // then
            assertThat(incremented.attemptCount()).isEqualTo(1);
            assertThat(incremented.nextAttemptAt()).isEqualTo(nextAttemptAt);
            assertThat(incremented.maxAttempts()).isEqualTo(state.maxAttempts());
        }
    }

    @Nested
    @DisplayName("recordSuccess()는")
    class Describe_record_success {

        @Test
        @DisplayName("attemptCount를 1 증가시키고 lastError를 지운다")
        void it_increments_attempt_count_and_clears_error() {
            // given
            final RetryState state = new RetryState(1, 3, NOW, "temporary error");

            // when
            final RetryState success = state.recordSuccess();

            // then
            assertThat(success.attemptCount()).isEqualTo(2);
            assertThat(success.lastError()).isNull();
            assertThat(success.nextAttemptAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("recordFailure()은")
    class Describe_record_failure {

        @Test
        @DisplayName("attemptCount를 1 증가시키고 에러 메시지와 nextAttemptAt을 기록한다")
        void it_increments_attempt_count_and_records_error() {
            // given
            final RetryState state = RetryState.initial(3, NOW);
            final String error = "connection timeout";
            final LocalDateTime nextAttemptAt = NOW.plusMinutes(2);

            // when
            final RetryState failed = state.recordFailure(error, nextAttemptAt);

            // then
            assertThat(failed.attemptCount()).isEqualTo(1);
            assertThat(failed.lastError()).isEqualTo(error);
            assertThat(failed.nextAttemptAt()).isEqualTo(nextAttemptAt);
        }
    }

    @Nested
    @DisplayName("withFailure()은")
    class Describe_with_failure {

        @Test
        @DisplayName("에러 메시지와 nextAttemptAt을 기록한다")
        void it_records_error_and_next_attempt_at() {
            // given
            final RetryState state = RetryState.initial(3, NOW);
            final String error = "connection timeout";
            final LocalDateTime nextAttemptAt = NOW.plusMinutes(2);

            // when
            final RetryState failed = state.withFailure(error, nextAttemptAt);

            // then
            assertThat(failed.lastError()).isEqualTo(error);
            assertThat(failed.nextAttemptAt()).isEqualTo(nextAttemptAt);
            assertThat(failed.attemptCount()).isEqualTo(state.attemptCount());
        }
    }

    @Nested
    @DisplayName("reset()은")
    class Describe_reset {

        @Test
        @DisplayName("attemptCount를 0으로 초기화하고 lastError를 null로 만든다")
        void it_resets_attempt_count_and_clears_error() {
            // given
            final RetryState exhausted = new RetryState(3, 3, NOW, "fatal error");
            final LocalDateTime retryAt = NOW.plusHours(1);

            // when
            final RetryState reset = exhausted.reset(retryAt);

            // then
            assertThat(reset.attemptCount()).isZero();
            assertThat(reset.lastError()).isNull();
            assertThat(reset.nextAttemptAt()).isEqualTo(retryAt);
            assertThat(reset.maxAttempts()).isEqualTo(exhausted.maxAttempts());
        }
    }
}
