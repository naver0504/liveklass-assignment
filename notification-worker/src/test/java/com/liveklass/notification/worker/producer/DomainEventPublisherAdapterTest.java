package com.liveklass.notification.worker.producer;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.domain.event.EmailNotificationEvent;
import com.liveklass.notification.worker.config.WorkerProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("DomainEventPublisherAdapter는")
class DomainEventPublisherAdapterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    @Mock
    private OutboxService outboxService;

    @Test
    @DisplayName("channelType 최소 계약을 만족하면 outbox를 저장한다")
    void it_saves_outbox_when_payload_is_valid() {
        // given
        final DomainEventPublisherAdapter adapter = new DomainEventPublisherAdapter(
                outboxService,
                new WorkerProperties(
                        new WorkerProperties.PollingProperties(true, 1000L, 50, 5L),
                        new WorkerProperties.RetryProperties(3, Map.of())
                )
        );
        final EmailNotificationEvent event = new EmailNotificationEvent(
                Topic.EMAIL_NOTIFICATION_REQUEST,
                1L,
                "ref-1",
                "결제 완료",
                "49,000원이 결제되었습니다.",
                "user@example.com",
                NOW
        );

        // when
        adapter.publish(event);

        // then
        verify(outboxService).createOutbox(
                anyString(),
                anyLong(),
                anyLong(),
                any(Topic.class),
                any(com.liveklass.common.event.ChannelType.class),
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                anyInt()
        );
    }

    @Test
    @DisplayName("channelType 최소 계약을 만족하지 않으면 outbox를 저장하지 않는다")
    void it_does_not_save_outbox_when_payload_is_invalid() {
        // given
        final DomainEventPublisherAdapter adapter = new DomainEventPublisherAdapter(
                outboxService,
                new WorkerProperties(
                        new WorkerProperties.PollingProperties(true, 1000L, 50, 5L),
                        new WorkerProperties.RetryProperties(3, Map.of())
                )
        );
        final DomainEvent event = new DomainEvent() {
            @Override
            public Topic topic() {
                return Topic.IN_APP_NOTIFICATION_REQUEST;
            }

            @Override
            public com.fasterxml.jackson.databind.JsonNode payload() {
                final ObjectNode payload = JsonNodeFactory.instance.objectNode();
                payload.put("title", " ");
                payload.put("body", "본문");
                return payload;
            }

            @Override
            public Long recipientId() {
                return 1L;
            }

            @Override
            public String referenceId() {
                return "ref-1";
            }

            @Override
            public LocalDateTime publishedAt() {
                return NOW;
            }
        };

        // when & then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> adapter.publish(event))
                .withMessageContaining("title");
        verify(outboxService, never()).createOutbox(
                anyString(),
                anyLong(),
                anyLong(),
                any(Topic.class),
                any(com.liveklass.common.event.ChannelType.class),
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                anyInt()
        );
    }
}
