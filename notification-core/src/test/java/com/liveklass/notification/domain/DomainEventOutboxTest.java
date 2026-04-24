package com.liveklass.notification.domain;

import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.fixture.DomainEventOutboxFixture;
import com.liveklass.notification.fixture.InAppNotificationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("UNIT_TEST")
@DisplayName("DomainEventOutbox는")
class DomainEventOutboxTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Nested
    @DisplayName("create()는")
    class Describe_create {

        @Test
        @DisplayName("초기 상태는 PENDING이고 lock은 비어있다")
        void it_creates_with_pending_status() {
            // given & when
            final DomainEventOutbox outbox = DomainEventOutboxFixture.pending();

            // then
            assertThat(outbox.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(outbox.retryState().attemptCount()).isZero();
            assertThat(outbox.lock().lockedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("claim()은")
    class Describe_claim {

        @Test
        @DisplayName("PENDING → PROCESSING으로 전이하고 lock만 설정된다")
        void it_transitions_to_processing() {
            // given
            final DomainEventOutbox outbox = DomainEventOutboxFixture.pending();

            // when
            final DomainEventOutbox claimed = outbox.claim(NOW);

            // then
            assertThat(claimed.status()).isEqualTo(OutboxStatus.PROCESSING);
            assertThat(claimed.lock().lockedAt()).isEqualTo(NOW);
            assertThat(claimed.retryState().attemptCount()).isZero();
        }

        @Test
        @DisplayName("PROCESSING 상태에서 다시 claim()하면 BadRequestException을 던진다")
        void it_throws_when_already_processing() {
            // given
            final DomainEventOutbox processing = DomainEventOutboxFixture.processing();

            // when & then
            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> processing.claim(NOW));
        }
    }

    @Nested
    @DisplayName("complete()는")
    class Describe_complete {

        @Test
        @DisplayName("PROCESSING → SENT로 전이하고 attemptCount를 증가시킨다")
        void it_transitions_to_sent() {
            // given
            final DomainEventOutbox processing = DomainEventOutboxFixture.processing();

            // when
            final DomainEventOutbox completed = processing.complete();

            // then
            assertThat(completed.status()).isEqualTo(OutboxStatus.SENT);
            assertThat(completed.lock().lockedAt()).isNull();
            assertThat(completed.retryState().attemptCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("fail()은")
    class Describe_fail {

        @Test
        @DisplayName("재시도 가능하면 PENDING으로 돌아가고 attemptCount와 lastError가 기록된다")
        void it_returns_to_pending_with_error_when_retryable() {
            // given
            final DomainEventOutbox processing = DomainEventOutboxFixture.processing();
            final String error = "connection timeout";
            final LocalDateTime nextAttemptAt = NOW.plusMinutes(1);

            // when
            final DomainEventOutbox failed = processing.fail(error, nextAttemptAt);

            // then
            assertThat(failed.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(failed.retryState().attemptCount()).isEqualTo(1);
            assertThat(failed.retryState().lastError()).isEqualTo(error);
            assertThat(failed.retryState().nextAttemptAt()).isEqualTo(nextAttemptAt);
            assertThat(failed.lock().lockedAt()).isNull();
        }

        @Test
        @DisplayName("maxAttempts 소진 시 DEAD_LETTER로 전이한다")
        void it_transitions_to_dead_letter_when_attempts_exhausted() {
            // given
            final DomainEventOutbox processing = DomainEventOutboxFixture.pendingWithMaxAttempts(1, "pay-002").claim(NOW);

            // when
            final DomainEventOutbox deadLetter = processing.fail("fatal error", NOW.plusMinutes(1));

            // then
            assertThat(deadLetter.status()).isEqualTo(OutboxStatus.DEAD_LETTER);
            assertThat(deadLetter.retryState().attemptCount()).isEqualTo(1);
            assertThat(deadLetter.retryState().lastError()).isEqualTo("fatal error");
        }
    }

    @Nested
    @DisplayName("recover()는")
    class Describe_recover {

        @Test
        @DisplayName("DEAD_LETTER → PENDING으로 전이하고 attemptCount가 0으로 초기화된다")
        void it_resets_to_pending_with_zero_attempt_count() {
            // given
            final DomainEventOutbox deadLetter = DomainEventOutboxFixture.deadLetter("pay-003");

            // when
            final DomainEventOutbox recovered = deadLetter.recover(NOW.plusHours(1));

            // then
            assertThat(recovered.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(recovered.retryState().attemptCount()).isZero();
            assertThat(recovered.retryState().lastError()).isNull();
        }
    }

    @Nested
    @DisplayName("Notification 엔티티는")
    class Describe_notification {

        @Test
        @DisplayName("초기 isRead는 false다")
        void it_creates_unread() {
            final InAppNotification notification = InAppNotificationFixture.unread();
            assertThat(notification.isRead()).isFalse();
        }

        @Test
        @DisplayName("markRead()는 멱등하다")
        void it_marks_read_idempotently() {
            // given
            final InAppNotification notification = InAppNotificationFixture.unread();

            // when
            final InAppNotification read1 = notification.markRead();
            final InAppNotification read2 = read1.markRead();

            // then
            assertThat(read1.isRead()).isTrue();
            assertThat(read2.isRead()).isTrue();
        }
    }
}
