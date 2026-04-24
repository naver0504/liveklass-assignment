package com.liveklass.enrollment.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수강 취소 요청")
public record EnrollmentCancelRequest(

        @Schema(description = "취소 사유", example = "개인 사정으로 인한 취소")
        String cancelReason
) {}
