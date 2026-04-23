package com.liveklass.lecture.domain;

import com.liveklass.lecture.domain.id.EnrollmentId;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.domain.id.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public record Enrollment(
        EnrollmentId id,
        LectureId lectureId,
        UserId userId,
        EnrollmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Enrollment {
        Objects.requireNonNull(lectureId, "lectureId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Enrollment create(
            final LectureId lectureId,
            final UserId userId,
            final LocalDateTime createdAt
    ) {
        final LocalDateTime now = Objects.requireNonNull(createdAt, "createdAt must not be null");
        return new Enrollment(null, lectureId, userId, EnrollmentStatus.ENROLLED, now, now);
    }

    public Enrollment cancel(final LocalDateTime cancelledAt) {
        Objects.requireNonNull(cancelledAt, "cancelledAt must not be null");

        if (status == EnrollmentStatus.CANCELLED) {
            return this;
        }

        return new Enrollment(id, lectureId, userId, EnrollmentStatus.CANCELLED, createdAt, cancelledAt);
    }
}
