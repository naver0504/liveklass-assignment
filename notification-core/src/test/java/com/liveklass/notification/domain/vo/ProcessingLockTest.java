package com.liveklass.notification.domain.vo;

import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.exception.OutboxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UNIT_TEST")
@DisplayName("ProcessingLock는")
class ProcessingLockTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Nested
    @DisplayName("불변식은")
    class Describe_invariant {

        @Test
        @DisplayName("PROCESSING이면 lockedAt이 반드시 있어야 한다")
        void it_requires_locked_at_when_processing() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new ProcessingLock(OutboxStatus.PROCESSING, null),
                    OutboxException.INVALID_PROCESSING_LOCK
            );
        }

        @Test
        @DisplayName("PROCESSING이 아니면 lockedAt이 없어야 한다")
        void it_requires_no_locked_at_when_not_processing() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new ProcessingLock(OutboxStatus.PENDING, NOW),
                    OutboxException.INVALID_PROCESSING_LOCK
            );
        }
    }

    @Nested
    @DisplayName("팩토리 메서드는")
    class Describe_factories {

        @Test
        @DisplayName("pending()은 PENDING + lockedAt null을 반환한다")
        void it_creates_pending() {
            // given & when
            final ProcessingLock lock = ProcessingLock.pending();

            // then
            assertThat(lock.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(lock.lockedAt()).isNull();
        }

        @Test
        @DisplayName("processing(lockedAt)은 PROCESSING + lockedAt을 반환한다")
        void it_creates_processing() {
            // given
            final LocalDateTime lockedAt = NOW;

            // when
            final ProcessingLock lock = ProcessingLock.processing(lockedAt);

            // then
            assertThat(lock.status()).isEqualTo(OutboxStatus.PROCESSING);
            assertThat(lock.lockedAt()).isEqualTo(lockedAt);
        }

        @Test
        @DisplayName("sent()는 SENT + lockedAt null을 반환한다")
        void it_creates_sent() {
            // given & when
            final ProcessingLock lock = ProcessingLock.sent();

            // then
            assertThat(lock.status()).isEqualTo(OutboxStatus.SENT);
            assertThat(lock.lockedAt()).isNull();
        }

        @Test
        @DisplayName("deadLetter()는 DEAD_LETTER + lockedAt null을 반환한다")
        void it_creates_dead_letter() {
            // given & when
            final ProcessingLock lock = ProcessingLock.deadLetter();

            // then
            assertThat(lock.status()).isEqualTo(OutboxStatus.DEAD_LETTER);
            assertThat(lock.lockedAt()).isNull();
        }

        @Test
        @DisplayName("of(status, lockedAt)는 상태에 맞는 lock 조합을 복원한다")
        void it_restores_lock_from_status_and_locked_at() {
            // when
            final ProcessingLock processing = ProcessingLock.of(OutboxStatus.PROCESSING, NOW);
            final ProcessingLock pending = ProcessingLock.of(OutboxStatus.PENDING, NOW.plusMinutes(1));

            // then
            assertThat(processing.status()).isEqualTo(OutboxStatus.PROCESSING);
            assertThat(processing.lockedAt()).isEqualTo(NOW);
            assertThat(pending.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(pending.lockedAt()).isNull();
        }
    }
}
