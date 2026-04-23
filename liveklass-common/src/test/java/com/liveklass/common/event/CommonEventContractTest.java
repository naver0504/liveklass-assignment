package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UNIT_TEST")
@DisplayName("공통 이벤트 계약 단위 테스트")
class CommonEventContractTest {

    @Nested
    @DisplayName("Topic enum은")
    class Describe_topic {

        @Test
        @DisplayName("문서 기준 이벤트 topic 값을 유지한다")
        void it_keeps_declared_topics() {
            // given
            final Topic[] topics = Topic.values();

            // when
            final String[] topicNames = new String[topics.length];
            for (int i = 0; i < topics.length; i++) {
                topicNames[i] = topics[i].name();
            }

            // then
            assertThat(topicNames).containsExactly(
                    "LECTURE_ENROLLMENT_COMPLETED",
                    "LECTURE_ENROLLMENT_CANCELLED",
                    "PAYMENT_CONFIRMED",
                    "LECTURE_STARTING_SOON"
            );
        }
    }

    @Nested
    @DisplayName("ChannelType enum은")
    class Describe_channel_type {

        @Test
        @DisplayName("문서 기준 발송 채널 값을 유지한다")
        void it_keeps_declared_channel_types() {
            // given
            final ChannelType[] channelTypes = ChannelType.values();

            // when
            final String[] channelTypeNames = new String[channelTypes.length];
            for (int i = 0; i < channelTypes.length; i++) {
                channelTypeNames[i] = channelTypes[i].name();
            }

            // then
            assertThat(channelTypeNames).containsExactly("EMAIL", "IN_APP");
        }
    }

    @Nested
    @DisplayName("DomainEvent 계약은")
    class Describe_domain_event {

        @Test
        @DisplayName("topic, recipientId, referenceId, publishedAt, payload를 그대로 노출한다")
        void it_exposes_declared_event_fields() {
            // given
            final Topic topic = Topic.PAYMENT_CONFIRMED;
            final Long recipientId = 1L;
            final String referenceId = "payment-1";
            final LocalDateTime publishedAt = LocalDateTime.of(2026, 4, 24, 12, 30);
            final JsonNode payload = JsonNodeFactory.instance.objectNode()
                    .put("title", "결제 완료")
                    .put("amount", 10000);
            final DomainEvent event = new TestDomainEvent(
                    topic,
                    recipientId,
                    referenceId,
                    publishedAt,
                    payload
            );

            // when
            final Topic actualTopic = event.topic();
            final Long actualRecipientId = event.recipientId();
            final String actualReferenceId = event.referenceId();
            final LocalDateTime actualPublishedAt = event.publishedAt();
            final JsonNode actualPayload = event.payload();

            // then
            assertThat(actualTopic).isEqualTo(topic);
            assertThat(actualRecipientId).isEqualTo(recipientId);
            assertThat(actualReferenceId).isEqualTo(referenceId);
            assertThat(actualPublishedAt).isEqualTo(publishedAt);
            assertThat(actualPayload).isEqualTo(payload);
        }
    }

    @Nested
    @DisplayName("DomainEventPublisher는")
    class Describe_domain_event_publisher {

        @Test
        @DisplayName("전달받은 DomainEvent를 publish 메서드로 위임한다")
        void it_publishes_domain_event() {
            // given
            final DomainEvent event = new TestDomainEvent(
                    Topic.LECTURE_ENROLLMENT_COMPLETED,
                    2L,
                    "enrollment-1",
                    LocalDateTime.of(2026, 4, 24, 13, 0),
                    JsonNodeFactory.instance.objectNode().put("lectureId", 10L)
            );
            final AtomicReference<DomainEvent> publishedEvent = new AtomicReference<>();
            final DomainEventPublisher publisher = publishedEvent::set;

            // when
            publisher.publish(event);

            // then
            assertThat(publishedEvent.get()).isSameAs(event);
        }
    }

    record TestDomainEvent(
            Topic topic,
            Long recipientId,
            String referenceId,
            LocalDateTime publishedAt,
            JsonNode payload
    ) implements DomainEvent {
    }
}
