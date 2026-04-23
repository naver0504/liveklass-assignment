package com.liveklass.lecture.fixture;

import com.liveklass.lecture.domain.Enrollment;
import com.liveklass.lecture.domain.EnrollmentStatus;
import com.liveklass.lecture.domain.id.EnrollmentId;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.domain.id.UserId;

import java.time.LocalDateTime;

public final class EnrollmentFixture {

    private static final EnrollmentId DEFAULT_ID = new EnrollmentId(1L);
    private static final LectureId DEFAULT_LECTURE_ID = new LectureId(10L);
    private static final UserId DEFAULT_USER_ID = new UserId(100L);
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.of(2026, 4, 1, 9, 0);
    private static final LocalDateTime DEFAULT_UPDATED_AT = LocalDateTime.of(2026, 4, 1, 9, 0);
    private static final LocalDateTime DEFAULT_CANCELLED_AT = LocalDateTime.of(2026, 4, 2, 9, 0);

    private EnrollmentFixture() {
    }

    public static Enrollment enrolled() {
        return enrollment(
                DEFAULT_ID,
                DEFAULT_LECTURE_ID,
                DEFAULT_USER_ID,
                EnrollmentStatus.ENROLLED,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT
        );
    }

    public static Enrollment enrolled(final LectureId lectureId, final UserId userId) {
        return enrollment(DEFAULT_ID, lectureId, userId, EnrollmentStatus.ENROLLED, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);
    }

    public static Enrollment cancelled(final LectureId lectureId, final UserId userId) {
        return enrollment(
                DEFAULT_ID,
                lectureId,
                userId,
                EnrollmentStatus.CANCELLED,
                DEFAULT_CREATED_AT,
                DEFAULT_CANCELLED_AT
        );
    }

    public static Enrollment enrollment(
            final EnrollmentId id,
            final LectureId lectureId,
            final UserId userId,
            final EnrollmentStatus status,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        return new Enrollment(id, lectureId, userId, status, createdAt, updatedAt);
    }
}
