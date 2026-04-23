package com.liveklass.notification.domain;

import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.notification.domain.vo.ProcessingLock;
import com.liveklass.notification.domain.vo.SentResult;
import com.liveklass.notification.fixture.DomainEventOutboxFixture;
import com.liveklass.notification.fixture.NotificationFixture;
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
        @DisplayName("초기 상태는 PENDING이고 sentResult는 null이다")
        void it_creates_with_pending_status_and_no_sent_result() {
            // given & when
            final DomainEventOutbox outbox = DomainEventOutboxFixture.pending();

            // then
            assertThat(outbox.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(outbox.retryState().attemptCount()).isZero();
            assertThat(outbox.sentResult()).isNull();
            assertThat(outbox.lock().lockedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("claim()은")
    class Describe_claim {

        @Test
        @DisplayName("PENDING → PROCESSING으로 전이하고 lock과 attemptCount가 갱신된다")
        void it_transitions_to_processing() {
            // given
            final DomainEventOutbox outbox = DomainEventOutboxFixture.pending();
            final LocalDateTime lockedAt = NOW;

            // when
            final DomainEventOutbox claimed = outbox.claim(lockedAt);

            // then
            assertThat(claimed.status()).isEqualTo(OutboxStatus.PROCESSING);
            assertThat(claimed.lock().lockedAt()).isEqualTo(lockedAt);
            assertThat(claimed.retryState().attemptCount()).isEqualTo(1);
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
        @DisplayName("PROCESSING → SENT로 전이하고 sentResult가 채워진다")
        void it_transitions_to_sent_with_sent_result() {
            // given
            final DomainEventOutbox processing = DomainEventOutboxFixture.processing();
            final String providerMessageId = "msg-xyz";
            final LocalDateTime sentAt = NOW.plusSeconds(2);

            // when
            final DomainEventOutbox completed = processing.complete(providerMessageId, sentAt);

            // then
            assertThat(completed.status()).isEqualTo(OutboxStatus.SENT);
            assertThat(completed.sentResult().sentAt()).isEqualTo(sentAt);
            assertThat(completed.sentResult().providerMessageId()).isEqualTo(providerMessageId);
            assertThat(completed.lock().lockedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("fail()은")
    class Describe_fail {

        @Test
        @DisplayName("재시도 가능하면 PENDING으로 돌아가고 lastError가 기록된다")
        void it_returns_to_pending_with_error_when_retryable() {
            // given
            final DomainEventOutbox processing = DomainEventOutboxFixture.processing();
            final String error = "connection timeout";
            final LocalDateTime nextAttemptAt = NOW.plusMinutes(1);

            // when
            final DomainEventOutbox failed = processing.fail(error, nextAttemptAt);

            // then
            assertThat(failed.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(failed.retryState().lastError()).isEqualTo(error);
            assertThat(failed.retryState().nextAttemptAt()).isEqualTo(nextAttemptAt);
            assertThat(failed.lock().lockedAt()).isNull();
        }

        @Test
        @DisplayName("maxAttempts 소진 시 DEAD_LETTER로 전이하고 sentResult는 null이다")
        void it_transitions_to_dead_letter_when_attempts_exhausted() {
            // given
            final DomainEventOutbox outbox = DomainEventOutboxFixture.pendingWithMaxAttempts(1, "pay-002");
            final DomainEventOutbox processing = outbox.claim(NOW);
            final String error = "fatal error";

            // when
            final DomainEventOutbox deadLetter = processing.fail(error, NOW.plusMinutes(1));

            // then
            assertThat(deadLetter.status()).isEqualTo(OutboxStatus.DEAD_LETTER);
            assertThat(deadLetter.retryState().lastError()).isEqualTo(error);
            assertThat(deadLetter.sentResult()).isNull();
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
            final LocalDateTime retryAt = NOW.plusHours(1);

            // when
            final DomainEventOutbox recovered = deadLetter.recover(retryAt);

            // then
            assertThat(recovered.status()).isEqualTo(OutboxStatus.PENDING);
            assertThat(recovered.retryState().attemptCount()).isZero();
            assertThat(recovered.retryState().nextAttemptAt()).isEqualTo(retryAt);
            assertThat(recovered.retryState().lastError()).isNull();
        }
    }

    @Nested
    @DisplayName("SENT ↔ sentResult 불변식은")
    class Describe_sent_result_invariant {

        @Test
        @DisplayName("SENT 상태가 되면 sentResult가 반드시 채워진다")
        void it_always_has_sent_result_when_sent() {
            // given
            final DomainEventOutbox sent = DomainEventOutboxFixture.sent();

            // when & then
            assertThat(sent.status()).isEqualTo(OutboxStatus.SENT);
            assertThat(sent.sentResult()).isNotNull();
        }

        @Test
        @DisplayName("PENDING/PROCESSING/DEAD_LETTER 상태에서는 sentResult가 null이다")
        void it_has_no_sent_result_when_not_sent() {
            assertThat(DomainEventOutboxFixture.pending().sentResult()).isNull();
            assertThat(DomainEventOutboxFixture.processing().sentResult()).isNull();
            assertThat(DomainEventOutboxFixture.deadLetter("pay-x").sentResult()).isNull();
        }
    }

    @Nested
    @DisplayName("Notification 엔티티는")
    class Describe_notification {

        @Test
        @DisplayName("초기 isRead는 false다")
        void it_creates_unread() {
            // given & when
            final Notification notification = NotificationFixture.unread();

            // then
            assertThat(notification.isRead()).isFalse();
        }

        @Test
        @DisplayName("markRead()는 멱등하다")
        void it_marks_read_idempotently() {
            // given
            final Notification notification = NotificationFixture.unread();

            // when
            final Notification read1 = notification.markRead();
            final Notification read2 = read1.markRead();

            // then
            assertThat(read1.isRead()).isTrue();
            assertThat(read2.isRead()).isTrue();
        }
    }
}
