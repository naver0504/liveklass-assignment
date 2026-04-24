package com.liveklass.notification.worker.recovery;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.fixture.DomainEventOutboxFixture;
import com.liveklass.notification.worker.config.WorkerProperties;
import com.liveklass.notification.worker.recovery.verifier.OutboxRecoveryVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("StuckOutboxRecoveryScheduler는")
class StuckOutboxRecoverySchedulerTest {

    @Mock
    private OutboxRecoveryVerifier inAppVerifier;

    @Mock
    private OutboxRecoveryVerifier emailVerifier;

    @Mock
    private OutboxService outboxService;

    private StuckOutboxRecoveryScheduler scheduler;

    @BeforeEach
    void setUp() {
        given(inAppVerifier.supports()).willReturn(ChannelType.IN_APP);
        given(emailVerifier.supports()).willReturn(ChannelType.EMAIL);
        scheduler = new StuckOutboxRecoveryScheduler(
                List.of(inAppVerifier, emailVerifier),
                outboxService,
                new WorkerProperties(
                        new WorkerProperties.PollingProperties(true, 1000L, 50, 5L),
                        new WorkerProperties.RetryProperties(3, java.util.Map.of())
                )
        );
    }

    @Test
    @DisplayName("IN_APP 처리 흔적이 있으면 stuck outbox를 SENT로 보정한다")
    void it_corrects_stuck_in_app_outbox_to_sent() {
        // given
        final DomainEventOutbox stuck = DomainEventOutboxFixture.processing()
                .toBuilder()
                .id(new OutboxId(1L))
                .eventRef(new EventRef(Topic.IN_APP_NOTIFICATION_REQUEST, ChannelType.IN_APP, "100"))
                .build();
        given(outboxService.findStuckOutboxes(any(LocalDateTime.class))).willReturn(List.of(stuck));
        given(inAppVerifier.exists(stuck)).willReturn(true);

        // when
        scheduler.recover();

        // then
        final ArgumentCaptor<List<DomainEventOutbox>> captor = ArgumentCaptor.forClass(List.class);
        verify(outboxService).saveAllProcessingResults(captor.capture());
        final List<DomainEventOutbox> recovered = captor.getValue();
        assertThat(recovered).hasSize(1);
        assertThat(recovered.getFirst().status()).isEqualTo(OutboxStatus.SENT);
        assertThat(recovered.getFirst().retryState().attemptCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("EMAIL provider가 idempotencyKey로 이미 발송됨을 확인하면 stuck outbox를 SENT로 보정한다")
    void it_corrects_stuck_email_outbox_to_sent_when_provider_confirms_delivery() {
        // given
        final DomainEventOutbox stuck = DomainEventOutboxFixture.processing()
                .toBuilder()
                .id(new OutboxId(2L))
                .eventRef(new EventRef(Topic.EMAIL_NOTIFICATION_REQUEST, ChannelType.EMAIL, "ref-2"))
                .idempotencyKey("EMAIL_NOTIFICATION_REQUEST:1:EMAIL:ref-2")
                .build();
        given(outboxService.findStuckOutboxes(any(LocalDateTime.class))).willReturn(List.of(stuck));
        given(emailVerifier.exists(stuck)).willReturn(true);

        // when
        scheduler.recover();

        // then
        final ArgumentCaptor<List<DomainEventOutbox>> captor = ArgumentCaptor.forClass(List.class);
        verify(outboxService).saveAllProcessingResults(captor.capture());
        final List<DomainEventOutbox> recovered = captor.getValue();
        assertThat(recovered).hasSize(1);
        assertThat(recovered.getFirst().status()).isEqualTo(OutboxStatus.SENT);
        assertThat(recovered.getFirst().retryState().attemptCount()).isEqualTo(1);
    }
}
