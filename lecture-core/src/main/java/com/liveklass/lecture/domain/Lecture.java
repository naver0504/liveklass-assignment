package com.liveklass.lecture.domain;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.lecture.domain.exception.LectureException;
import com.liveklass.lecture.domain.id.LectureId;

import java.time.LocalDateTime;

public record Lecture(
        LectureId id,
        String title,
        LocalDateTime startAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Lecture {
        requireTitle(title);
        requireStartAt(startAt);
        requireCreatedAt(createdAt);
        requireUpdatedAt(updatedAt);
    }

    private static void requireTitle(final String title) {
        if (title == null) {
            throw ExceptionCreator.create(LectureException.LECTURE_TITLE_REQUIRED, "field: title");
        }
        if (title.isBlank()) {
            throw ExceptionCreator.create(LectureException.LECTURE_TITLE_BLANK, "field: title");
        }
    }

    private static void requireStartAt(final LocalDateTime startAt) {
        if (startAt == null) {
            throw ExceptionCreator.create(LectureException.LECTURE_START_AT_REQUIRED, "field: startAt");
        }
    }

    private static void requireCreatedAt(final LocalDateTime createdAt) {
        if (createdAt == null) {
            throw ExceptionCreator.create(LectureException.LECTURE_CREATED_AT_REQUIRED, "field: createdAt");
        }
    }

    private static void requireUpdatedAt(final LocalDateTime updatedAt) {
        if (updatedAt == null) {
            throw ExceptionCreator.create(LectureException.LECTURE_UPDATED_AT_REQUIRED, "field: updatedAt");
        }
    }
}
