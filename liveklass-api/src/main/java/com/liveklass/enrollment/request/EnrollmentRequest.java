package com.liveklass.enrollment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "수강 신청 요청")
public record EnrollmentRequest(

        @Schema(description = "강의 ID", example = "1")
        @NotNull Long lectureId
) {}
