package com.liveklass.enrollment.api;

import com.liveklass.common.dto.ErrorResponse;
import com.liveklass.enrollment.request.EnrollmentCancelRequest;
import com.liveklass.enrollment.request.EnrollmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Enrollment", description = "수강 신청 API")
@RequestMapping("/api/enrollments")
public interface EnrollmentApi {

    @Operation(summary = "수강 신청",
            description = "강의에 수강 신청합니다. 중복 신청 시 409 오류가 발생합니다.")
    @ApiResponse(responseCode = "201", description = "수강 신청 완료")
    @ApiResponse(responseCode = "400", description = "요청 값 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "강의 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "이미 수강 신청된 강의",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void enroll(
            @Parameter(description = "수강생 ID") @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody EnrollmentRequest request
    );

    @Operation(summary = "수강 취소",
            description = "수강 신청을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "수강 취소 완료")
    @ApiResponse(responseCode = "403", description = "본인 수강 신청 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "수강 신청 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{enrollmentId}")
    void cancel(
            @Parameter(description = "수강 신청 ID") @PathVariable Long enrollmentId,
            @Parameter(description = "수강생 ID") @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) EnrollmentCancelRequest request
    );
}
