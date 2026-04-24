package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.InAppPayload;
import com.liveklass.common.event.Topic;
import com.liveklass.lecture.domain.exception.EnrollmentException;

import java.time.LocalDateTime;

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
        requireValue(enrollmentId, "enrollmentId");
        requireValue(lectureId, "lectureId");
        requireValue(userId, "userId");
        requireText(lectureTitle, "lectureTitle");
        requireText(cancelReason, "cancelReason");
        requireValue(recipientId, "recipientId");
        requireText(referenceId, "referenceId");
        requireValue(publishedAt, "publishedAt");
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

    private static void requireValue(final Object value, final String fieldName) {
        if (value == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_EVENT_REQUIRED, "field: " + fieldName);
        }
    }

    private static void requireText(final String value, final String fieldName) {
        if (value == null) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_EVENT_REQUIRED, "field: " + fieldName);
        }
        if (value.isBlank()) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_EVENT_BLANK, "field: " + fieldName);
        }
    }
}
