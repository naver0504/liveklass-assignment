package com.liveklass.lecture.application.dto;

import java.time.LocalDateTime;

public record LectureEnrollmentSummary(
        Long lectureId,
        String lectureTitle,
        LocalDateTime lectureStartAt,
        Long enrollmentId,
        Long userId
) {}
