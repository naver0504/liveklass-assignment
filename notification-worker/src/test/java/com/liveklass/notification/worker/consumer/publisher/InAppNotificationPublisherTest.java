package com.liveklass.notification.worker.consumer.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.worker.consumer.PublishResult;
import com.liveklass.notification.application.service.InAppNotificationService;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.event.InAppNotificationEvent;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.fixture.DomainEventOutboxFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("InAppNotificationPublisher는")
class InAppNotificationPublisherTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Mock
    private InAppNotificationService inAppNotificationService;

    private InAppNotificationPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new InAppNotificationPublisher(inAppNotificationService, new ObjectMapper());
    }

    @Test
    @DisplayName("payload metadata의 publishedAt을 인앱 알림 발행 시각으로 사용한다")
    void it_uses_published_at_from_payload_metadata() {
        // given
        final DomainEventOutbox outbox = DomainEventOutboxFixture.pendingInAppRequest()
                .toBuilder()
                .id(new OutboxId(10L))
                .eventRef(new EventRef(Topic.IN_APP_NOTIFICATION_REQUEST, ChannelType.IN_APP, "100"))
                .payload(new InAppNotificationEvent(
                        Topic.IN_APP_NOTIFICATION_REQUEST,
                        1L,
                        "100",
                        "수강 신청 완료",
                        "Spring Boot 수강 신청이 완료되었습니다.",
                        NOW
                ).payload().toString())
                .build();

        // when
        final PublishResult result = publisher.publish(outbox);

        // then
        assertThat(result).isInstanceOf(PublishResult.Success.class);
        final ArgumentCaptor<LocalDateTime> publishedAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(inAppNotificationService).createInAppNotification(
                eq(10L),
                eq(1L),
                eq("수강 신청 완료"),
                eq("Spring Boot 수강 신청이 완료되었습니다."),
                publishedAtCaptor.capture(),
                any(LocalDateTime.class)
        );
        assertThat(publishedAtCaptor.getValue()).isEqualTo(NOW);
    }
}
