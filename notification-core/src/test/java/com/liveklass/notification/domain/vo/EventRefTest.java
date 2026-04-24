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
@DisplayName("EventRef는")
class EventRefTest {

    @Nested
    @DisplayName("생성은")
    class Describe_create {

        @Test
        @DisplayName("topic, channelType, referenceId를 그대로 보유한다")
        void it_holds_all_fields() {
            // given
            final Topic topic = Topic.PAYMENT_CONFIRMED;
            final ChannelType channelType = ChannelType.EMAIL;
            final String referenceId = "pay-001";

            // when
            final EventRef eventRef = new EventRef(topic, channelType, referenceId);

            // then
            assertThat(eventRef.topic()).isEqualTo(topic);
            assertThat(eventRef.channelType()).isEqualTo(channelType);
            assertThat(eventRef.referenceId()).isEqualTo(referenceId);
        }

        @Test
        @DisplayName("topic이 null이면 NPE를 던진다")
        void it_throws_when_topic_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> new EventRef(null, ChannelType.EMAIL, "ref"))
                    .withMessageContaining("topic");
        }

        @Test
        @DisplayName("channelType이 null이면 NPE를 던진다")
        void it_throws_when_channel_type_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> new EventRef(Topic.PAYMENT_CONFIRMED, null, "ref"))
                    .withMessageContaining("channelType");
        }

        @Test
        @DisplayName("referenceId가 null이면 NPE를 던진다")
        void it_throws_when_reference_id_is_null() {
            assertThatNullPointerException()
                    .isThrownBy(() -> new EventRef(Topic.PAYMENT_CONFIRMED, ChannelType.EMAIL, null))
                    .withMessageContaining("referenceId");
        }
    }
}
