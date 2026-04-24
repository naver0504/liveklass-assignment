package com.liveklass.notification.api;

import com.liveklass.common.dto.ErrorResponse;
import com.liveklass.notification.response.InAppNotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "InAppNotification", description = "인앱 알림 API")
@RequestMapping("/api/in-app-notifications")
public interface InAppNotificationApi {

    @Operation(summary = "인앱 알림 목록 조회",
            description = "수신자 기준 인앱 알림 목록을 조회합니다. isRead 파라미터로 읽음/안읽음을 필터링할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    List<InAppNotificationResponse> list(
            @Parameter(description = "수신자 ID") @RequestHeader("X-User-Id") Long recipientId,
            @Parameter(description = "읽음 여부 필터 (생략 시 전체)") @RequestParam boolean isRead
    );

    @Operation(summary = "인앱 알림 읽음 처리",
            description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "읽음 처리 완료")
    @ApiResponse(responseCode = "403", description = "본인 알림 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "알림 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{id}/read")
    void markAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long id,
            @Parameter(description = "수신자 ID") @RequestHeader("X-User-Id") Long userId
    );
}
