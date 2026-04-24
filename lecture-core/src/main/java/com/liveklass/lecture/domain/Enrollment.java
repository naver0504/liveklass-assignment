package com.liveklass.lecture.domain;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.lecture.domain.enums.EnrollmentStatus;
import com.liveklass.lecture.domain.exception.EnrollmentException;
import com.liveklass.lecture.domain.id.EnrollmentId;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.domain.id.UserId;

import java.time.LocalDateTime;

public record Enrollment(
        EnrollmentId id,
        LectureId lectureId,
        UserId userId,
        EnrollmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Enrollment {
        requireLectureId(lectureId);
        requireUserId(userId);
        requireStatus(status);
        requireCreatedAt(createdAt);
        requireUpdatedAt(updatedAt);
    }

    public static Enrollment create(
            final LectureId lectureId,
            final UserId userId,
            final LocalDateTime createdAt
    ) {
        return new Enrollment(null, lectureId, userId, EnrollmentStatus.ENROLLED, createdAt, createdAt);
    }

    public Enrollment cancel(final LocalDateTime cancelledAt) {
        requireCancelledAt(cancelledAt);

        if (status == EnrollmentStatus.CANCELLED) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_CANCEL_NOT_ALLOWED, "status: " + status);
        }

        return new Enrollment(id, lectureId, userId, EnrollmentStatus.CANCELLED, createdAt, cancelledAt);
    }

    private static void requireLectureId(final LectureId lectureId) {
        if (lectureId == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_LECTURE_ID_REQUIRED, "field: lectureId");
        }
    }

    private static void requireUserId(final UserId userId) {
        if (userId == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_USER_ID_REQUIRED, "field: userId");
        }
    }

    private static void requireStatus(final EnrollmentStatus status) {
        if (status == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_STATUS_REQUIRED, "field: status");
        }
    }

    private static void requireCreatedAt(final LocalDateTime createdAt) {
        if (createdAt == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_CREATED_AT_REQUIRED, "field: createdAt");
        }
    }

    private static void requireUpdatedAt(final LocalDateTime updatedAt) {
        if (updatedAt == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_UPDATED_AT_REQUIRED, "field: updatedAt");
        }
    }

    private static void requireCancelledAt(final LocalDateTime cancelledAt) {
        if (cancelledAt == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_CANCELLED_AT_REQUIRED, "field: cancelledAt");
        }
    }
}
