package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.InAppPayload;
import com.liveklass.common.event.Topic;

import java.time.LocalDateTime;
import java.util.Objects;

public record EnrollmentCancelledEvent(
        Long enrollmentId,
        Long lectureId,
        Long userId,
        String lectureTitle,
        String cancelReason,
        Long recipientId,
        String referenceId,
        LocalDateTime publishedAt
) implements DomainEvent {
    public EnrollmentCancelledEvent {
        Objects.requireNonNull(enrollmentId, "enrollmentId must not be null");
        Objects.requireNonNull(lectureId, "lectureId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(lectureTitle, "lectureTitle must not be null");
        Objects.requireNonNull(cancelReason, "cancelReason must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(referenceId, "referenceId must not be null");
        Objects.requireNonNull(publishedAt, "publishedAt must not be null");

        if (lectureTitle.isBlank()) {
            throw new IllegalArgumentException("lectureTitle must not be blank");
        }
        if (cancelReason.isBlank()) {
            throw new IllegalArgumentException("cancelReason must not be blank");
        }
        if (referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId must not be blank");
        }
    }

    @Override
    public Topic topic() {
        return Topic.LECTURE_ENROLLMENT_CANCELLED;
    }

    @Override
    public JsonNode payload() {
        return InAppPayload.builder(
                        "수강 신청이 취소되었습니다",
                        "취소된 강의: " + lectureTitle
                )
                .metadata("severity", "INFO")
                .metadata("enrollmentId", enrollmentId)
                .metadata("cancelReason", cancelReason)
                .metadata("cancelledAt", publishedAt.toString())
                .build();
    }
}
