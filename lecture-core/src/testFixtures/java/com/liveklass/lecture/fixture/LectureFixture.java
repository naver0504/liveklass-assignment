package com.liveklass.lecture.fixture;

import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.id.LectureId;

import java.time.LocalDateTime;

public final class LectureFixture {

    private static final LectureId DEFAULT_ID = new LectureId(1L);
    private static final String DEFAULT_TITLE = "Spring Boot 실전";
    private static final LocalDateTime DEFAULT_START_AT = LocalDateTime.of(2026, 5, 1, 10, 0);
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.of(2026, 4, 1, 9, 0);
    private static final LocalDateTime DEFAULT_UPDATED_AT = LocalDateTime.of(2026, 4, 1, 9, 0);

    private LectureFixture() {
    }

    public static Lecture lecture() {
        return lecture(DEFAULT_ID, DEFAULT_TITLE, DEFAULT_START_AT, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);
    }

    public static Lecture lecture(final String title) {
        return lecture(DEFAULT_ID, title, DEFAULT_START_AT, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);
    }

    public static Lecture lecture(final LocalDateTime startAt) {
        return lecture(DEFAULT_ID, DEFAULT_TITLE, startAt, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);
    }

    public static Lecture lecture(
            final LectureId id,
            final String title,
            final LocalDateTime startAt,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        return new Lecture(id, title, startAt, createdAt, updatedAt);
    }
}
