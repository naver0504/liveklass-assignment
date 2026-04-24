package com.liveklass.lecture.response;

import com.liveklass.lecture.domain.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "강의 응답")
public record LectureResponse(

        @Schema(description = "강의 ID", example = "1")
        Long id,

        @Schema(description = "강의 제목", example = "Spring Boot 실전 강의")
        String title,

        @Schema(description = "강의 시작 시각", example = "2026-05-01T10:00:00")
        LocalDateTime startAt,

        @Schema(description = "생성 시각")
        LocalDateTime createdAt
) {
    public static LectureResponse from(final Lecture lecture) {
        return new LectureResponse(
                lecture.id().value(),
                lecture.title(),
                lecture.startAt(),
                lecture.createdAt()
        );
    }
}
