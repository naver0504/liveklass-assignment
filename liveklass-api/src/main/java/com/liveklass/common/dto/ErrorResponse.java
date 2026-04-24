package com.liveklass.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오류 응답")
public record ErrorResponse(
        @Schema(description = "오류 코드", example = "ENROLLMENT_012")
        String errorCode,
        @Schema(description = "오류 메시지", example = "이미 수강 신청한 강의입니다.")
        String message
) {}
