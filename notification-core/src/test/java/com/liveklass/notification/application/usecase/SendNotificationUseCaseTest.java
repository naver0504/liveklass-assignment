package com.liveklass.notification.application.usecase;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.notification.domain.event.EmailNotificationEvent;
import com.liveklass.notification.domain.event.InAppNotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("SendNotificationUseCase는")
class SendNotificationUseCaseTest {

    @InjectMocks
    private SendNotificationUseCase sendNotificationUseCase;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Test
    @DisplayName("예약 시각이 있으면 event.publishedAt에 그대로 담아 발행한다")
    void it_publishes_event_with_scheduled_at() {
        // given
        final LocalDateTime scheduledAt = LocalDateTime.of(2026, 4, 25, 9, 0);

        // when
        sendNotificationUseCase.send(
                1L,
                ChannelType.EMAIL,
                100L,
                null,
                "결제가 완료되었습니다.",
                "결제 완료",
                "user@example.com",
                scheduledAt
        );

        // then
        final ArgumentCaptor<DomainEvent> captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(captor.capture());
        final DomainEvent published = captor.getValue();
        assertThat(published).isInstanceOf(EmailNotificationEvent.class);
        assertThat(published.publishedAt()).isEqualTo(scheduledAt);
    }

    @Test
    @DisplayName("예약 시각이 없으면 현재 시각 기반 이벤트를 발행한다")
    void it_publishes_event_with_now_when_schedule_is_missing() {
        // given
        final LocalDateTime before = LocalDateTime.now();

        // when
        sendNotificationUseCase.send(
                1L,
                ChannelType.IN_APP,
                100L,
                "수강 신청 완료",
                "Spring Boot 수강 신청이 완료되었습니다.",
                null,
                null,
                null
        );
        final LocalDateTime after = LocalDateTime.now();

        // then
        final ArgumentCaptor<DomainEvent> captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(captor.capture());
        final DomainEvent published = captor.getValue();
        assertThat(published).isInstanceOf(InAppNotificationEvent.class);
        assertThat(published.publishedAt()).isBetween(before, after);
    }
}
