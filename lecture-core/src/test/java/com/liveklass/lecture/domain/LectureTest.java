package com.liveklass.lecture.domain;

import com.liveklass.common.test.ExceptionAssertions;
import com.liveklass.lecture.domain.exception.LectureException;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.fixture.LectureFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UNIT_TEST")
@DisplayName("Lecture는")
class LectureTest {

    @Nested
    @DisplayName("생성자는")
    class Describe_constructor {

        @Test
        @DisplayName("유효한 입력이면 필드를 그대로 보존한다")
        void it_keeps_given_fields() {
            // given
            final Lecture lecture = LectureFixture.lecture();

            // when
            final LectureId id = lecture.id();
            final String title = lecture.title();
            final LocalDateTime startAt = lecture.startAt();

            // then
            assertThat(id.value()).isEqualTo(1L);
            assertThat(title).isEqualTo("Spring Boot 실전");
            assertThat(startAt).isEqualTo(LocalDateTime.of(2026, 5, 1, 10, 0));
        }

        @Test
        @DisplayName("title이 null이면 예외를 던진다")
        void it_throws_when_title_is_null() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> LectureFixture.lecture((String) null),
                    LectureException.LECTURE_TITLE_REQUIRED
            );
        }

        @Test
        @DisplayName("title이 blank면 예외를 던진다")
        void it_throws_when_title_is_blank() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> LectureFixture.lecture("   "),
                    LectureException.LECTURE_TITLE_BLANK
            );
        }

        @Test
        @DisplayName("startAt이 null이면 예외를 던진다")
        void it_throws_when_start_at_is_null() {
            ExceptionAssertions.assertThatExceptionOfType(
                    () -> LectureFixture.lecture((LocalDateTime) null),
                    LectureException.LECTURE_START_AT_REQUIRED
            );
        }
    }
}
