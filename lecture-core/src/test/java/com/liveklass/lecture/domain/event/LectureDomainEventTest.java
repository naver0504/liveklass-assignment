package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.Topic;
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
                    1L,
                    10L,
                    100L,
                    "Spring Boot 실전",
                    100L,
                    "1",
                    LocalDateTime.of(2026, 4, 24, 10, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.LECTURE_ENROLLMENT_COMPLETED);
            assertThat(payload.get("title").asText()).isEqualTo("수강 신청이 완료되었습니다");
            assertThat(payload.get("body").asText()).contains("Spring Boot 실전");
            assertThat(payload.get("metadata").get("screen").asText()).isEqualTo("LECTURE_DETAIL");
            assertThat(payload.get("metadata").get("enrollmentId").asLong()).isEqualTo(1L);
            assertThat(payload.get("metadata").get("lectureId").asLong()).isEqualTo(10L);
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
                    1L,
                    10L,
                    100L,
                    "Spring Boot 실전",
                    "USER_REQUEST",
                    100L,
                    "1",
                    LocalDateTime.of(2026, 4, 24, 11, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.LECTURE_ENROLLMENT_CANCELLED);
            assertThat(payload.get("title").asText()).isEqualTo("수강 신청이 취소되었습니다");
            assertThat(payload.get("body").asText()).contains("Spring Boot 실전");
            assertThat(payload.get("metadata").get("severity").asText()).isEqualTo("INFO");
            assertThat(payload.get("metadata").get("cancelReason").asText()).isEqualTo("USER_REQUEST");
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
                    "pay-001",
                    100L,
                    1L,
                    49000L,
                    "KRW",
                    "user@example.com",
                    100L,
                    "pay-001",
                    LocalDateTime.of(2026, 4, 24, 12, 0)
            );

            // when
            final JsonNode payload = event.payload();

            // then
            assertThat(event.topic()).isEqualTo(Topic.PAYMENT_CONFIRMED);
            assertThat(payload.get("subject").asText()).isEqualTo("결제가 완료되었습니다");
            assertThat(payload.get("body").get("headline").asText()).isEqualTo("결제가 완료되었습니다");
            assertThat(payload.get("metadata").get("recipientEmail").asText()).isEqualTo("user@example.com");
            assertThat(payload.get("metadata").get("paymentId").asText()).isEqualTo("pay-001");
            assertThat(payload.get("metadata").get("amount").asLong()).isEqualTo(49000L);
        }
    }
}
