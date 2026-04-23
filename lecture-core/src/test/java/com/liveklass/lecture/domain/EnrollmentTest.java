package com.liveklass.lecture.domain;

import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.domain.id.UserId;
import com.liveklass.lecture.fixture.EnrollmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UNIT_TEST")
@DisplayName("Enrollment는")
class EnrollmentTest {

    @Nested
    @DisplayName("create()는")
    class Describe_create {

        @Test
        @DisplayName("기본 상태를 ENROLLED로 생성하고 createdAt과 updatedAt을 동일하게 둔다")
        void it_creates_enrolled_status_by_default() {
            // given
            final LectureId lectureId = new LectureId(10L);
            final UserId userId = new UserId(100L);
            final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 1, 9, 0);

            // when
            final Enrollment enrollment = Enrollment.create(lectureId, userId, createdAt);

            // then
            assertThat(enrollment.id()).isNull();
            assertThat(enrollment.lectureId()).isEqualTo(lectureId);
            assertThat(enrollment.userId()).isEqualTo(userId);
            assertThat(enrollment.status()).isEqualTo(EnrollmentStatus.ENROLLED);
            assertThat(enrollment.createdAt()).isEqualTo(createdAt);
            assertThat(enrollment.updatedAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("cancel()은")
    class Describe_cancel {

        @Test
        @DisplayName("ENROLLED를 CANCELLED로 전이하고 updatedAt을 갱신한다")
        void it_transitions_to_cancelled() {
            // given
            final Enrollment enrollment = EnrollmentFixture.enrolled();
            final LocalDateTime cancelledAt = LocalDateTime.of(2026, 4, 2, 9, 0);

            // when
            final Enrollment cancelled = enrollment.cancel(cancelledAt);

            // then
            assertThat(cancelled.id()).isEqualTo(enrollment.id());
            assertThat(cancelled.lectureId()).isEqualTo(enrollment.lectureId());
            assertThat(cancelled.userId()).isEqualTo(enrollment.userId());
            assertThat(cancelled.status()).isEqualTo(EnrollmentStatus.CANCELLED);
            assertThat(cancelled.createdAt()).isEqualTo(enrollment.createdAt());
            assertThat(cancelled.updatedAt()).isEqualTo(cancelledAt);
        }

        @Test
        @DisplayName("이미 CANCELLED면 같은 인스턴스를 반환한다")
        void it_returns_same_instance_when_already_cancelled() {
            // given
            final Enrollment cancelledEnrollment = EnrollmentFixture.cancelled(new LectureId(10L), new UserId(100L));
            final LocalDateTime cancelledAt = LocalDateTime.of(2026, 4, 3, 9, 0);

            // when
            final Enrollment result = cancelledEnrollment.cancel(cancelledAt);

            // then
            assertThat(result).isSameAs(cancelledEnrollment);
        }
    }
}
