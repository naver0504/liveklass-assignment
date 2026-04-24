package com.liveklass.lecture.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "강의 생성 요청")
public record LectureCreateRequest(

        @Schema(description = "강의 제목", example = "Spring Boot 실전 강의")
        @NotBlank String title,

        @Schema(description = "강의 시작 시각", example = "2026-05-01T10:00:00")
        @NotNull LocalDateTime startAt
) {}
