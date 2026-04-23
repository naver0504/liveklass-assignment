package com.liveklass.notification.application.service;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.notification.application.repository.OutboxRepository;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.exception.OutboxException;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.fixture.DomainEventOutboxFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxService는")
class OutboxServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Nested
    @DisplayName("createOutbox()는")
    class Describe_createOutbox {

        @Test
        @DisplayName("outbox를 저장하고 PENDING 상태로 반환한다")
        void it_saves_and_returns_pending_outbox() {
            // given
            final DomainEventOutbox pending = DomainEventOutboxFixture.pending();
            given(outboxRepository.save(any(DomainEventOutbox.class))).willReturn(pending);

            // when
            final DomainEventOutbox result = outboxService.createOutbox(
                    DomainEventOutboxFixture.defaultIdempotencyKey().value(),
                    pending.requesterId(),
                    pending.recipientId(),
                    pending.eventRef().topic(),
                    pending.eventRef().channelType(),
                    pending.eventRef().referenceId(),
                    pending.payload(),
                    NOW,
                    3
            );

            // then
            assertThat(result.status()).isEqualTo(OutboxStatus.PENDING);
            verify(outboxRepository).save(any(DomainEventOutbox.class));
        }
    }

    @Nested
    @DisplayName("claimAll()은")
    class Describe_claimAll {

        @Test
        @DisplayName("PENDING outbox 목록을 PROCESSING으로 전이하고 일괄 저장한다")
        void it_claims_all_pending_outboxes() {
            // given
            final List<DomainEventOutbox> pending = List.of(
                    DomainEventOutboxFixture.pending(),
                    DomainEventOutboxFixture.pendingInAppRequest()
            );
            given(outboxRepository.saveAll(any())).willAnswer(inv -> inv.getArgument(0));

            // when
            final List<DomainEventOutbox> result = outboxService.claimAll(pending, NOW);

            // then
            assertThat(result).hasSize(2);
            result.forEach(o -> {
                assertThat(o.status()).isEqualTo(OutboxStatus.PROCESSING);
                assertThat(o.lock().lockedAt()).isEqualTo(NOW);
            });
        }
    }

    @Nested
    @DisplayName("saveAll()은")
    class Describe_saveAll {

        @Test
        @DisplayName("outbox 목록을 일괄 저장한다")
        void it_saves_all_outboxes() {
            // given
            final List<DomainEventOutbox> outboxes = List.of(
                    DomainEventOutboxFixture.processing(),
                    DomainEventOutboxFixture.processing()
            );
            given(outboxRepository.saveAll(any())).willAnswer(inv -> inv.getArgument(0));

            // when
            final List<DomainEventOutbox> result = outboxService.saveAll(outboxes);

            // then
            assertThat(result).hasSize(2);
            verify(outboxRepository).saveAll(outboxes);
        }
    }

    @Nested
    @DisplayName("findRequestNotificationById()는")
    class Describe_findRequestNotificationById {

        @Test
        @DisplayName("알림 요청 토픽의 outbox를 본인 조건으로 조회한다")
        void it_returns_notification_request_outbox_for_owner() {
            // given
            final Long outboxId = 1L;
            final Long recipientId = 1L;
            final DomainEventOutbox pending = DomainEventOutboxFixture.pendingInAppRequest();
            given(outboxRepository.findById(new OutboxId(outboxId))).willReturn(java.util.Optional.of(pending));

            // when
            final DomainEventOutbox result = outboxService.findRequestNotificationById(outboxId, recipientId);

            // then
            assertThat(result.status()).isEqualTo(OutboxStatus.PENDING);
        }

        @Test
        @DisplayName("존재하지 않는 outboxId면 OUTBOX_NOT_FOUND 예외를 던진다")
        void it_throws_when_not_found() {
            // given
            final Long outboxId = 999L;
            given(outboxRepository.findById(new OutboxId(outboxId))).willReturn(java.util.Optional.empty());

            // when & then
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> outboxService.findRequestNotificationById(outboxId, 1L),
                    OutboxException.OUTBOX_NOT_FOUND
            );
        }

        @Test
        @DisplayName("알림 요청 토픽이 아니면 OUTBOX_NOT_FOUND 예외를 던진다")
        void it_throws_when_topic_is_not_notification_request() {
            // given
            final Long outboxId = 1L;
            final DomainEventOutbox pending = DomainEventOutboxFixture.pending();
            given(outboxRepository.findById(new OutboxId(outboxId))).willReturn(java.util.Optional.of(pending));

            // when & then
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> outboxService.findRequestNotificationById(outboxId, 1L),
                    OutboxException.OUTBOX_NOT_FOUND
            );
        }

        @Test
        @DisplayName("본인의 요청이 아니면 OUTBOX_ACCESS_DENIED 예외를 던진다")
        void it_throws_when_not_owner() {
            // given
            final Long outboxId = 1L;
            final DomainEventOutbox pending = DomainEventOutboxFixture.pendingInAppRequest();
            given(outboxRepository.findById(new OutboxId(outboxId))).willReturn(java.util.Optional.of(pending));

            // when & then
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> outboxService.findRequestNotificationById(outboxId, 99L),
                    OutboxException.OUTBOX_ACCESS_DENIED
            );
        }
    }

    @Nested
    @DisplayName("findPendingOutboxes()는")
    class Describe_findPendingOutboxes {

        @Test
        @DisplayName("50개 limit으로 PENDING outbox 목록을 반환한다")
        void it_returns_pending_outboxes_with_limit() {
            // given
            final List<DomainEventOutbox> pending = List.of(
                    DomainEventOutboxFixture.pending(),
                    DomainEventOutboxFixture.pendingInAppRequest()
            );
            given(outboxRepository.findTop50PendingBefore(NOW)).willReturn(pending);

            // when
            final List<DomainEventOutbox> result = outboxService.findPendingOutboxes(NOW);

            // then
            assertThat(result).hasSize(2);
            result.forEach(o -> assertThat(o.status()).isEqualTo(OutboxStatus.PENDING));
        }
    }
}
