package com.liveklass.notification.application.service;

import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.event.EmailNotificationEvent;
import com.liveklass.notification.domain.event.InAppNotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("NotificationService는")
class NotificationServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Nested
    @DisplayName("requestInApp()은")
    class Describe_requestInApp {

        @Test
        @DisplayName("IN_APP_NOTIFICATION 토픽으로 InAppNotificationEvent를 발행한다")
        void it_publishes_in_app_notification_event() {
            // given
            final Long recipientId = 1L;
            final Long referenceId = 100L;
            final String title = "수강 신청 완료";
            final String body = "Spring Boot 수강 신청이 완료되었습니다.";

            // when
            notificationService.requestInAppNotification(recipientId, referenceId, title, body, NOW);

            // then
            final ArgumentCaptor<DomainEvent> captor = ArgumentCaptor.forClass(DomainEvent.class);
            verify(eventPublisher).publish(captor.capture());
            final DomainEvent published = captor.getValue();
            assertThat(published).isInstanceOf(InAppNotificationEvent.class);
            final InAppNotificationEvent event = (InAppNotificationEvent) published;
            assertThat(event.topic()).isEqualTo(Topic.IN_APP_NOTIFICATION_REQUEST);
            assertThat(event.recipientId()).isEqualTo(recipientId);
            assertThat(event.referenceId()).isEqualTo(String.valueOf(referenceId));
            assertThat(event.title()).isEqualTo(title);
            assertThat(event.body()).isEqualTo(body);
            assertThat(event.publishedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("requestEmail()은")
    class Describe_requestEmail {

        @Test
        @DisplayName("EMAIL_NOTIFICATION 토픽으로 EmailNotificationEvent를 발행한다")
        void it_publishes_email_notification_event() {
            // given
            final Long recipientId = 1L;
            final Long referenceId = 1L;
            final String subject = "결제 완료 안내";
            final String body = "49,000원이 결제되었습니다.";
            final String recipientEmail = "user@example.com";

            // when
            notificationService.requestEmailNotification(recipientId, referenceId, subject, body, recipientEmail, NOW);

            // then
            final ArgumentCaptor<DomainEvent> captor = ArgumentCaptor.forClass(DomainEvent.class);
            verify(eventPublisher).publish(captor.capture());
            final DomainEvent published = captor.getValue();
            assertThat(published).isInstanceOf(EmailNotificationEvent.class);
            final EmailNotificationEvent event = (EmailNotificationEvent) published;
            assertThat(event.topic()).isEqualTo(Topic.EMAIL_NOTIFICATION_REQUEST);
            assertThat(event.recipientId()).isEqualTo(recipientId);
            assertThat(event.referenceId()).isEqualTo(String.valueOf(referenceId));
            assertThat(event.subject()).isEqualTo(subject);
            assertThat(event.body()).isEqualTo(body);
            assertThat(event.recipientEmail()).isEqualTo(recipientEmail);
            assertThat(event.publishedAt()).isEqualTo(NOW);
        }
    }
}
