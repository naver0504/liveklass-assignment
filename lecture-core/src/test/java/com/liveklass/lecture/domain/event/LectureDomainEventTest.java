package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.Topic;
import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.lecture.domain.exception.EnrollmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UNIT_TEST")
@DisplayName("lecture-core 도메인 이벤트는")
class LectureDomainEventTest {

    @Nested
    @DisplayName("EnrollmentCompletedEvent는")
    class Describe_enrollment_completed_event {

        @Test
        @DisplayName("IN_APP 최소 계약을 만족하는 payload를 반환한다")
        void it_returns_in_app_payload() {
            // given
            final EnrollmentCompletedEvent event = new EnrollmentCompletedEvent(
                    1L, 10L, 100L, "Spring Boot 실전", 100L, "1",
                    LocalDateTime.of(2026, 4, 24, 10, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.LECTURE_ENROLLMENT_COMPLETED);
            assertThat(payload.get("title").asText()).isEqualTo("수강 신청이 완료되었습니다");
            assertThat(payload.get("body").asText()).contains(event.lectureTitle());
            assertThat(payload.get("metadata").get("screen").asText()).isEqualTo("LECTURE_DETAIL");
            assertThat(payload.get("metadata").get("enrollmentId").asLong()).isEqualTo(event.enrollmentId());
            assertThat(payload.get("metadata").get("lectureId").asLong()).isEqualTo(event.lectureId());
        }

        @Test
        @DisplayName("lectureTitle이 blank이면 예외를 던진다")
        void it_throws_when_lecture_title_is_blank() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new EnrollmentCompletedEvent(1L, 10L, 100L, "   ", 100L, "1",
                            LocalDateTime.of(2026, 4, 24, 10, 0)),
                    EnrollmentException.ENROLLMENT_EVENT_BLANK
            );
        }

        @Test
        @DisplayName("referenceId가 null이면 예외를 던진다")
        void it_throws_when_reference_id_is_null() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new EnrollmentCompletedEvent(1L, 10L, 100L, "Spring Boot 실전", 100L, null,
                            LocalDateTime.of(2026, 4, 24, 10, 0)),
                    EnrollmentException.ENROLLMENT_EVENT_REQUIRED
            );
        }
    }

    @Nested
    @DisplayName("EnrollmentCancelledEvent는")
    class Describe_enrollment_cancelled_event {

        @Test
        @DisplayName("취소 안내용 IN_APP payload를 반환한다")
        void it_returns_cancelled_in_app_payload() {
            // given
            final EnrollmentCancelledEvent event = new EnrollmentCancelledEvent(
                    1L, 10L, 100L, "Spring Boot 실전", "USER_REQUEST", 100L, "1",
                    LocalDateTime.of(2026, 4, 24, 11, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.LECTURE_ENROLLMENT_CANCELLED);
            assertThat(payload.get("title").asText()).isEqualTo("수강 신청이 취소되었습니다");
            assertThat(payload.get("body").asText()).contains(event.lectureTitle());
            assertThat(payload.get("metadata").get("severity").asText()).isEqualTo("INFO");
            assertThat(payload.get("metadata").get("cancelReason").asText()).isEqualTo(event.cancelReason());
        }

        @Test
        @DisplayName("cancelReason이 blank이면 예외를 던진다")
        void it_throws_when_cancel_reason_is_blank() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new EnrollmentCancelledEvent(1L, 10L, 100L, "Spring Boot 실전", "   ", 100L, "1",
                            LocalDateTime.of(2026, 4, 24, 11, 0)),
                    EnrollmentException.ENROLLMENT_EVENT_BLANK
            );
        }
    }

    @Nested
    @DisplayName("LectureStartingSoonEvent는")
    class Describe_lecture_starting_soon_event {

        @Test
        @DisplayName("D-1 안내용 IN_APP payload를 반환한다")
        void it_returns_starting_soon_in_app_payload() {
            // given
            final LectureStartingSoonEvent event = new LectureStartingSoonEvent(
                    10L, 100L, "Spring Boot 실전",
                    LocalDateTime.of(2026, 5, 1, 10, 0),
                    100L, "10",
                    LocalDateTime.of(2026, 4, 30, 10, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.LECTURE_STARTING_SOON);
            assertThat(payload.get("title").asText()).isEqualTo("내일 강의가 시작됩니다");
            assertThat(payload.get("body").asText()).contains(event.lectureTitle());
            assertThat(payload.get("body").asText()).contains("5월 1일 10:00");
            assertThat(payload.get("metadata").get("lectureId").asLong()).isEqualTo(event.lectureId());
            assertThat(payload.get("metadata").get("startAt").asText()).isEqualTo(event.startAt().toString());
        }

        @Test
        @DisplayName("lectureTitle이 null이면 예외를 던진다")
        void it_throws_when_lecture_title_is_null() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new LectureStartingSoonEvent(10L, 100L, null,
                            LocalDateTime.of(2026, 5, 1, 10, 0), 100L, "10",
                            LocalDateTime.of(2026, 4, 30, 10, 0)),
                    EnrollmentException.ENROLLMENT_EVENT_REQUIRED
            );
        }

        @Test
        @DisplayName("startAt이 null이면 예외를 던진다")
        void it_throws_when_start_at_is_null() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new LectureStartingSoonEvent(10L, 100L, "Spring Boot 실전",
                            null, 100L, "10",
                            LocalDateTime.of(2026, 4, 30, 10, 0)),
                    EnrollmentException.ENROLLMENT_EVENT_REQUIRED
            );
        }
    }

    @Nested
    @DisplayName("PaymentConfirmedEvent는")
    class Describe_payment_confirmed_event {

        @Test
        @DisplayName("EMAIL 최소 계약을 만족하는 payload를 반환한다")
        void it_returns_email_payload() {
            // given
            final PaymentConfirmedEvent event = new PaymentConfirmedEvent(
                    "pay-001", 100L, 1L, 49000L, "KRW", "user@example.com", 100L, "pay-001",
                    LocalDateTime.of(2026, 4, 24, 12, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.PAYMENT_CONFIRMED);
            assertThat(payload.get("subject").asText()).isEqualTo("결제가 완료되었습니다");
            assertThat(payload.get("body").get("headline").asText()).isEqualTo("결제가 완료되었습니다");
            assertThat(payload.get("metadata").get("recipientEmail").asText()).isEqualTo(event.recipientEmail());
            assertThat(payload.get("metadata").get("paymentId").asText()).isEqualTo(event.paymentId());
            assertThat(payload.get("metadata").get("amount").asLong()).isEqualTo(event.amount());
        }

        @Test
        @DisplayName("amount가 음수이면 예외를 던진다")
        void it_throws_when_amount_is_negative() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new PaymentConfirmedEvent(
                            "pay-001", 100L, 1L, -1L, "KRW", "user@example.com", 100L, "pay-001",
                            LocalDateTime.of(2026, 4, 24, 12, 0)),
                    EnrollmentException.PAYMENT_EVENT_AMOUNT_INVALID
            );
        }

        @Test
        @DisplayName("recipientEmail이 blank이면 예외를 던진다")
        void it_throws_when_recipient_email_is_blank() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> new PaymentConfirmedEvent(
                            "pay-001", 100L, 1L, 49000L, "KRW", "   ", 100L, "pay-001",
                            LocalDateTime.of(2026, 4, 24, 12, 0)),
                    EnrollmentException.PAYMENT_EVENT_BLANK
            );
        }
    }
}
