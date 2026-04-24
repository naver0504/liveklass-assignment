package com.liveklass.notification.controller;

import com.liveklass.notification.api.InAppNotificationApi;
import com.liveklass.notification.application.service.InAppNotificationService;
import com.liveklass.notification.response.InAppNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InAppNotificationController implements InAppNotificationApi {

    private final InAppNotificationService inAppNotificationService;

    @Override
    public List<InAppNotificationResponse> list(final Long recipientId, final boolean isRead) {
        return inAppNotificationService.findAllByRecipientId(recipientId, isRead)
                .stream()
                .map(InAppNotificationResponse::from)
                .toList();
    }

    @Override
    public void markAsRead(final Long id, final Long userId) {
        inAppNotificationService.findById(id, userId);
        inAppNotificationService.markAsRead(id);
    }
}
