package com.liveklass.notification.domain.vo;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@Tag("UNIT_TEST")
@DisplayName("IdempotencyKey는")
class IdempotencyKeyTest {

    @Nested
    @DisplayName("of()는")
    class Describe_of {

        @Test
        @DisplayName("{topic}:{recipientId}:{channelType}:{referenceId} 포맷으로 value를 반환한다")
        void it_returns_formatted_value() {
            // given
            final Topic topic = Topic.PAYMENT_CONFIRMED;
            final Long recipientId = 42L;
            final ChannelType channelType = ChannelType.EMAIL;
            final String referenceId = "pay-001";

            // when
            final IdempotencyKey key = IdempotencyKey.of(topic, recipientId, channelType, referenceId);

            // then
            assertThat(key.value()).isEqualTo("PAYMENT_CONFIRMED:42:EMAIL:pay-001");
        }

        @Test
        @DisplayName("동일한 입력으로 생성한 두 IdempotencyKey는 equals가 true다")
        void it_equals_same_inputs() {
            // given
            final Topic topic = Topic.LECTURE_ENROLLMENT_COMPLETED;
            final Long recipientId = 1L;
            final ChannelType channelType = ChannelType.IN_APP;
            final String referenceId = "enroll-100";

            // when
            final IdempotencyKey key1 = IdempotencyKey.of(topic, recipientId, channelType, referenceId);
            final IdempotencyKey key2 = IdempotencyKey.of(topic, recipientId, channelType, referenceId);

            // then
            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("topic이 null이면 NPE를 던진다")
        void it_throws_when_topic_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> IdempotencyKey.of(null, 1L, ChannelType.EMAIL, "ref-1"))
                    .withMessageContaining("topic");
        }

        @Test
        @DisplayName("recipientId가 null이면 NPE를 던진다")
        void it_throws_when_recipient_id_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> IdempotencyKey.of(Topic.PAYMENT_CONFIRMED, null, ChannelType.EMAIL, "ref-1"))
                    .withMessageContaining("recipientId");
        }

        @Test
        @DisplayName("channelType이 null이면 NPE를 던진다")
        void it_throws_when_channel_type_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> IdempotencyKey.of(Topic.PAYMENT_CONFIRMED, 1L, null, "ref-1"))
                    .withMessageContaining("channelType");
        }

        @Test
        @DisplayName("referenceId가 null이면 NPE를 던진다")
        void it_throws_when_reference_id_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> IdempotencyKey.of(Topic.PAYMENT_CONFIRMED, 1L, ChannelType.EMAIL, null))
                    .withMessageContaining("referenceId");
        }
    }
}
