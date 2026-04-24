package com.liveklass.notification.request;

import com.liveklass.common.event.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "알림 발송 요청")
public record NotificationSendRequest(

        @Schema(description = "수신자 ID", example = "100")
        @NotNull Long recipientId,

        @Schema(description = "발송 채널 (IN_APP | EMAIL)", example = "IN_APP")
        @NotNull ChannelType channelType,

        @Schema(description = "참조 ID (엔티티 식별자)", example = "1")
        @NotNull Long referenceId,

        @Schema(description = "알림 제목 (IN_APP 필수)", example = "수강 신청이 완료되었습니다")
        String title,

        @Schema(description = "알림 본문 (공통 필수)", example = "Spring Boot 실전 강의 수강이 완료되었습니다.")
        @NotBlank String body,

        @Schema(description = "이메일 제목 (EMAIL 필수)", example = "결제가 완료되었습니다")
        String subject,

        @Schema(description = "수신자 이메일 (EMAIL 필수)", example = "user@example.com")
        String recipientEmail,

        @Schema(description = "발송 예약 시각 (생략 시 즉시 발송)", example = "2026-04-25T09:00:00")
        LocalDateTime scheduledAt
) {}
