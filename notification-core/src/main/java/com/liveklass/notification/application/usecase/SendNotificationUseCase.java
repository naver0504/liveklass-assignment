package com.liveklass.notification.application.usecase;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.event.EmailNotificationEvent;
import com.liveklass.notification.domain.event.InAppNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SendNotificationUseCase {

    private final DomainEventPublisher eventPublisher;

    public void send(
            final Long recipientId,
            final ChannelType channelType,
            final Long referenceId,
            final String title,
            final String body,
            final String subject,
            final String recipientEmail,
            final LocalDateTime scheduledAt
    ) {
        final DomainEvent event = buildEvent(
                channelType, recipientId, String.valueOf(referenceId),
                title, body, subject, recipientEmail, scheduledAt
        );

        eventPublisher.publish(event);
    }

    private static DomainEvent buildEvent(
            final ChannelType channelType,
            final Long recipientId,
            final String referenceId,
            final String title,
            final String body,
            final String subject,
            final String recipientEmail,
            final LocalDateTime now
    ) {
        return switch (channelType) {
            case IN_APP -> new InAppNotificationEvent(
                    Topic.IN_APP_NOTIFICATION_REQUEST, recipientId, referenceId,
                    title, body, now != null ? now : LocalDateTime.now()
            );
            case EMAIL -> new EmailNotificationEvent(
                    Topic.EMAIL_NOTIFICATION_REQUEST, recipientId, referenceId,
                    subject, body, recipientEmail, now != null ? now : LocalDateTime.now()
            );
        };
    }
}
