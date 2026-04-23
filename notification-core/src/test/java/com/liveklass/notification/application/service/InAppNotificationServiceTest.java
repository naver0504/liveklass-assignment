package com.liveklass.notification.application.service;

import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.notification.application.repository.InAppNotificationRepository;
import com.liveklass.notification.domain.InAppNotification;
import com.liveklass.notification.domain.exception.NotificationException;
import com.liveklass.notification.domain.id.NotificationId;
import com.liveklass.notification.fixture.InAppNotificationFixture;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("InAppNotificationService는")
class InAppNotificationServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @InjectMocks
    private InAppNotificationService notificationService;

    @Mock
    private InAppNotificationRepository notificationRepository;

    @Nested
    @DisplayName("createNotification()은")
    class Describe_createNotification {

        @Test
        @DisplayName("InAppNotification을 생성하고 저장한다")
        void it_saves_and_returns_notification() {
            // given
            final InAppNotification unread = InAppNotificationFixture.unread(10L, 1L);
            given(notificationRepository.save(any(InAppNotification.class))).willReturn(unread);

            // when
            final InAppNotification result = notificationService.createInAppNotification(
                    10L, 1L, "수강 신청 완료", "Spring Boot 수강 신청이 완료되었습니다.", NOW, NOW
            );

            // then
            assertThat(result.isRead()).isFalse();
            assertThat(result.recipientId()).isEqualTo(1L);
            assertThat(result.outboxId()).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("markAsRead()는")
    class Describe_markAsRead {

        @Test
        @DisplayName("알림을 읽음 상태로 전이하고 저장한다")
        void it_marks_notification_as_read() {
            // given
            final Long notificationId = 1L;
            final InAppNotification unread = InAppNotificationFixture.unread();
            given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.of(unread));
            given(notificationRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            // when
            final InAppNotification result = notificationService.markAsRead(notificationId);

            // then
            assertThat(result.isRead()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 notificationId면 NOTIFICATION_NOT_FOUND 예외를 던진다")
        void it_throws_when_not_found() {
            // given
            final Long notificationId = 999L;
            given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.empty());

            // when & then
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> notificationService.markAsRead(notificationId),
                    NotificationException.NOTIFICATION_NOT_FOUND
            );
        }
    }

    @Nested
    @DisplayName("findById()는")
    class Describe_findById {

        @Test
        @DisplayName("본인의 알림을 조회한다")
        void it_returns_notification_by_id_for_owner() {
            // given
            final Long notificationId = 1L;
            final Long recipientId = 1L;
            final InAppNotification unread = InAppNotificationFixture.unread(10L, recipientId);
            given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.of(unread));

            // when
            final InAppNotification result = notificationService.findById(notificationId, recipientId);

            // then
            assertThat(result.recipientId()).isEqualTo(recipientId);
        }

        @Test
        @DisplayName("존재하지 않는 notificationId면 NOTIFICATION_NOT_FOUND 예외를 던진다")
        void it_throws_when_not_found() {
            // given
            final Long notificationId = 999L;
            given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.empty());

            // when & then
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> notificationService.findById(notificationId, 1L),
                    NotificationException.NOTIFICATION_NOT_FOUND
            );
        }

        @Test
        @DisplayName("본인의 알림이 아니면 NOTIFICATION_ACCESS_DENIED 예외를 던진다")
        void it_throws_when_not_owner() {
            // given
            final Long notificationId = 1L;
            final InAppNotification unread = InAppNotificationFixture.unread(10L, 1L);
            given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.of(unread));

            // when & then
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> notificationService.findById(notificationId, 99L),
                    NotificationException.NOTIFICATION_ACCESS_DENIED
            );
        }
    }

    @Nested
    @DisplayName("findAllByRecipientId()는")
    class Describe_findAllByRecipientId {

        @Test
        @DisplayName("수신자 ID로 전체 알림 목록을 반환한다")
        void it_returns_all_notifications_for_recipient() {
            // given
            final Long recipientId = 1L;
            final List<InAppNotification> notifications = List.of(
                    InAppNotificationFixture.unread(10L, recipientId),
                    InAppNotificationFixture.unread(11L, recipientId)
            );
            given(notificationRepository.findAllByRecipientId(recipientId)).willReturn(notifications);

            // when
            final List<InAppNotification> result = notificationService.findAllByRecipientId(recipientId);

            // then
            assertThat(result).hasSize(2);
            result.forEach(n -> assertThat(n.recipientId()).isEqualTo(recipientId));
        }

        @Test
        @DisplayName("isRead 필터로 읽음/안읽음 알림 목록을 반환한다")
        void it_returns_filtered_notifications_by_read_status() {
            // given
            final Long recipientId = 1L;
            final List<InAppNotification> unreadList = List.of(
                    InAppNotificationFixture.unread(10L, recipientId)
            );
            given(notificationRepository.findAllByRecipientIdAndIsRead(recipientId, false)).willReturn(unreadList);

            // when
            final List<InAppNotification> result = notificationService.findAllByRecipientId(recipientId, false);

            // then
            assertThat(result).hasSize(1);
            result.forEach(n -> assertThat(n.isRead()).isFalse());
        }
    }
}
