package com.liveklass.notification.worker.consumer.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.worker.consumer.PublishResult;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.domain.vo.IdempotencyKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailNotificationPublisher는")
class EmailNotificationPublisherTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Mock
    private EmailNotificationSender emailNotificationSender;

    private final Executor directExecutor = Runnable::run;

    @Test
    @DisplayName("provider에 raw idempotencyKey를 전달한다")
    void it_passes_raw_idempotency_key_to_sender() {
        // given
        final EmailNotificationPublisher publisher =
                new EmailNotificationPublisher(new ObjectMapper(), directExecutor, emailNotificationSender);
        final DomainEventOutbox outbox = DomainEventOutbox.create(
                IdempotencyKey.of(Topic.EMAIL_NOTIFICATION_REQUEST, 1L, ChannelType.EMAIL, "ref-1"),
                1L,
                1L,
                new EventRef(Topic.EMAIL_NOTIFICATION_REQUEST, ChannelType.EMAIL, "ref-1"),
                "{\"subject\":\"결제 완료\",\"body\":{\"headline\":\"완료\",\"amount\":49000},\"metadata\":{\"recipientEmail\":\"user@example.com\"}}",
                NOW,
                3
        );

        // when
        final PublishResult result = publisher.publish(outbox);

        // then
        assertThat(result).isInstanceOf(PublishResult.Success.class);
        verify(emailNotificationSender).send(
                "user@example.com",
                "결제 완료",
                "{\"headline\":\"완료\",\"amount\":49000}",
                "EMAIL_NOTIFICATION_REQUEST:1:EMAIL:ref-1"
        );
    }
}
