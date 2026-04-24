package com.liveklass.notification.response;

import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.enums.OutboxStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "알림 처리 상태 응답")
public record NotificationStatusResponse(

        @Schema(description = "알림 요청 ID", example = "1")
        Long id,

        @Schema(description = "토픽", example = "IN_APP_NOTIFICATION_REQUEST")
        String topic,

        @Schema(description = "발송 채널", example = "IN_APP")
        String channelType,

        @Schema(description = "참조 ID", example = "1")
        String referenceId,

        @Schema(description = "현재 상태", example = "발송완료",
                allowableValues = {"대기중", "처리중", "발송완료", "최종실패"})
        String status,

        @Schema(description = "시도 횟수", example = "1")
        int attemptCount,

        @Schema(description = "최대 시도 횟수", example = "3")
        int maxAttempts,

        @Schema(description = "다음 시도 예정 시각")
        LocalDateTime nextAttemptAt,

        @Schema(description = "마지막 오류 내용")
        String lastError
) {
    public static NotificationStatusResponse from(final DomainEventOutbox outbox) {
        return new NotificationStatusResponse(
                outbox.id().id(),
                outbox.eventRef().topic().name(),
                outbox.eventRef().channelType().name(),
                outbox.eventRef().referenceId(),
                toDisplayStatus(outbox.status()),
                outbox.retryState().attemptCount(),
                outbox.retryState().maxAttempts(),
                outbox.retryState().nextAttemptAt(),
                outbox.retryState().lastError()
        );
    }

    private static String toDisplayStatus(final OutboxStatus status) {
        return switch (status) {
            case PENDING -> "대기중";
            case PROCESSING -> "처리중";
            case SENT -> "발송완료";
            case DEAD_LETTER -> "최종실패";
        };
    }
}
