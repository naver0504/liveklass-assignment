package com.liveklass.lecture.domain;

import com.liveklass.lecture.domain.id.LectureId;

import java.time.LocalDateTime;
import java.util.Objects;

public record Lecture(
        LectureId id,
        String title,
        LocalDateTime startAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Lecture {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(startAt, "startAt must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }
}
