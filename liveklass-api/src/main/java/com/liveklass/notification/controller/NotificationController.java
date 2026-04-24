package com.liveklass.notification.controller;

import com.liveklass.notification.api.NotificationApi;
import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.application.usecase.SendNotificationUseCase;
import com.liveklass.notification.request.NotificationSendRequest;
import com.liveklass.notification.response.NotificationStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final SendNotificationUseCase sendNotificationUseCase;
    private final OutboxService outboxService;

    @Override
    public void send(final NotificationSendRequest request) {
        sendNotificationUseCase.send(
                request.recipientId(),
                request.channelType(),
                request.referenceId(),
                request.title(),
                request.body(),
                request.subject(),
                request.recipientEmail(),
                request.scheduledAt()
        );
    }

    @Override
    public NotificationStatusResponse getStatus(final Long id, final Long requesterId) {
        return NotificationStatusResponse.from(
                outboxService.findRequestNotificationById(id, requesterId)
        );
    }

    @Override
    public void manualRetry(final Long id) {
        outboxService.manualRetry(id);
    }
}
