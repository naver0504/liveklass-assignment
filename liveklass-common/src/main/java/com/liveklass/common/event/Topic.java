package com.liveklass.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    LECTURE_ENROLLMENT_COMPLETED (ChannelType.IN_APP),
    LECTURE_ENROLLMENT_CANCELLED (ChannelType.IN_APP),
    PAYMENT_CONFIRMED            (ChannelType.EMAIL),
    LECTURE_STARTING_SOON        (ChannelType.IN_APP),
    IN_APP_NOTIFICATION_REQUEST  (ChannelType.IN_APP),
    EMAIL_NOTIFICATION_REQUEST   (ChannelType.EMAIL);

    private final ChannelType channelType;
}
