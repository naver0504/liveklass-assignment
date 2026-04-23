package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.InAppPayload;
import com.liveklass.common.event.Topic;

import java.time.LocalDateTime;
import java.util.Objects;

public record EnrollmentCompletedEvent(
        Long enrollmentId,
        Long lectureId,
        Long userId,
        String lectureTitle,
        Long recipientId,
        String referenceId,
        LocalDateTime publishedAt
) implements DomainEvent {
    public EnrollmentCompletedEvent {
        Objects.requireNonNull(enrollmentId, "enrollmentId must not be null");
        Objects.requireNonNull(lectureId, "lectureId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(lectureTitle, "lectureTitle must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        Objects.requireNonNull(publishedAt, "publishedAt must not be null");

        if (lectureTitle.isBlank()) {
            throw new IllegalArgumentException("lectureTitle must not be blank");
        }
        if (referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId must not be blank");
        }
    }

    @Override
    public Topic topic() {
        return Topic.LECTURE_ENROLLMENT_COMPLETED;
    }

    @Override
    public JsonNode payload() {
        return InAppPayload.builder(
                        "수강 신청이 완료되었습니다",
                        lectureTitle + " 수강 신청이 완료되었습니다."
                )
                .metadata("screen", "LECTURE_DETAIL")
                .metadata("enrollmentId", enrollmentId)
                .metadata("lectureId", lectureId)
                .metadata("lectureTitle", lectureTitle)
                .metadata("enrolledAt", publishedAt.toString())
                .build();
    }
}
