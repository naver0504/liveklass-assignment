package com.liveklass.notification.api;

import com.liveklass.common.dto.ErrorResponse;
import com.liveklass.notification.request.NotificationSendRequest;
import com.liveklass.notification.response.NotificationStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 발송 API")
@RequestMapping("/api/notifications")
public interface NotificationApi {

    @Operation(summary = "알림 발송 요청",
            description = "IN_APP 또는 EMAIL 알림 발송을 비동기로 요청합니다. 즉시 발송되지 않으며 outbox를 통해 처리됩니다.")
    @ApiResponse(responseCode = "202", description = "요청 접수 완료")
    @ApiResponse(responseCode = "400", description = "요청 값 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    void send(@Valid @RequestBody NotificationSendRequest request);

    @Operation(summary = "알림 처리 상태 조회",
            description = "알림 요청 ID로 현재 처리 상태(대기중/처리중/발송완료/최종실패)를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "알림 요청 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    NotificationStatusResponse getStatus(
            @Parameter(description = "알림 요청 ID") @PathVariable Long id,
            @Parameter(description = "요청자 ID") @RequestHeader("X-User-Id") Long requesterId
    );

    @Operation(summary = "최종 실패 알림 수동 재시도",
            description = "DEAD_LETTER 상태의 알림을 대기 상태로 전환해 재처리를 예약합니다. 재시도 횟수는 초기화됩니다.")
    @ApiResponse(responseCode = "200", description = "재시도 예약 완료")
    @ApiResponse(responseCode = "404", description = "알림 요청 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/retry")
    void manualRetry(@Parameter(description = "알림 요청 ID") @PathVariable Long id);
}
