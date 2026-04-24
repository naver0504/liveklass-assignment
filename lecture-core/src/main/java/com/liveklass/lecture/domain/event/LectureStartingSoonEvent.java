package com.liveklass.lecture.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.InAppPayload;
import com.liveklass.common.event.Topic;
import com.liveklass.lecture.domain.exception.EnrollmentException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record LectureStartingSoonEvent(
        Long lectureId,
        Long userId,
        String lectureTitle,
        LocalDateTime startAt,
        Long recipientId,
        String referenceId,
        LocalDateTime publishedAt
) implements DomainEvent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M월 d일 HH:mm");

    public LectureStartingSoonEvent {
        requireValue(lectureId, "lectureId");
        requireValue(userId, "userId");
        requireText(lectureTitle, "lectureTitle");
        requireValue(startAt, "startAt");
        requireValue(recipientId, "recipientId");
        requireText(referenceId, "referenceId");
        requireValue(publishedAt, "publishedAt");
    }

    @Override
    public Topic topic() {
        return Topic.LECTURE_STARTING_SOON;
    }

    @Override
    public Long recipientId() {
        return recipientId;
    }

    @Override
    public String referenceId() {
        return referenceId;
    }

    @Override
    public JsonNode payload() {
        return InAppPayload.builder(
                        "내일 강의가 시작됩니다",
                        lectureTitle + " 강의가 " + startAt.format(FORMATTER) + "에 시작됩니다."
                )
                .metadata("screen", "LECTURE_DETAIL")
                .metadata("lectureId", lectureId)
                .metadata("lectureTitle", lectureTitle)
                .metadata("startAt", startAt.toString())
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
