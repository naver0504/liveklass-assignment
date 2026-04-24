package com.liveklass.notification.response;

import com.liveklass.notification.domain.InAppNotification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "인앱 알림 응답")
public record InAppNotificationResponse(

        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "알림 제목", example = "수강 신청이 완료되었습니다")
        String title,

        @Schema(description = "알림 본문", example = "Spring Boot 실전 강의 수강이 완료되었습니다.")
        String body,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead,

        @Schema(description = "발행 시각")
        LocalDateTime publishedAt,

        @Schema(description = "생성 시각")
        LocalDateTime createdAt
) {
    public static InAppNotificationResponse from(final InAppNotification notification) {
        return new InAppNotificationResponse(
                notification.id().id(),
                notification.title(),
                notification.body(),
                notification.isRead(),
                notification.publishedAt(),
                notification.createdAt()
        );
    }
}
