package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@Tag("UNIT_TEST")
@DisplayName("EmailPayload 빌더는")
class EmailPayloadTest {

    @Nested
    @DisplayName("텍스트 본문 builder()는")
    class Describe_text_builder {

        @Test
        @DisplayName("subject와 body를 JSON 최상위 필드로 반환한다")
        void it_returns_subject_and_body_as_root_fields() {
            // given
            final String subject = "[Live Klass] 결제 완료";
            final String body = "49,000원 결제가 승인되었습니다.";
            final String recipientEmail = "user@example.com";

            // when
            final JsonNode payload = EmailPayload.builder(subject, body, recipientEmail).build();

            // then
            assertThat(payload.get("subject").asText()).isEqualTo(subject);
            assertThat(payload.get("body").asText()).isEqualTo(body);
        }

        @Test
        @DisplayName("recipientEmail이 metadata.recipientEmail에 들어간다")
        void it_puts_recipient_email_under_metadata() {
            // given
            final String recipientEmail = "user@example.com";

            // when
            final JsonNode payload = EmailPayload.builder("제목", "본문", recipientEmail).build();

            // then
            assertThat(payload.get("metadata").get("recipientEmail").asText())
                    .isEqualTo(recipientEmail);
        }

        @Test
        @DisplayName("metadata() 체이닝으로 추가한 값이 기존 recipientEmail과 함께 들어간다")
        void it_merges_extra_metadata_with_recipient_email() {
            // given
            final String recipientEmail = "user@example.com";
            final String paymentId = "pay_001";
            final long amount = 49000L;

            // when
            final JsonNode payload = EmailPayload.builder("제목", "본문", recipientEmail)
                    .metadata("paymentId", paymentId)
                    .metadata("amount", amount)
                    .build();

            // then
            final JsonNode metadata = payload.get("metadata");
            assertThat(metadata.get("recipientEmail").asText()).isEqualTo(recipientEmail);
            assertThat(metadata.get("paymentId").asText()).isEqualTo(paymentId);
            assertThat(metadata.get("amount").asLong()).isEqualTo(amount);
        }
    }

    @Nested
    @DisplayName("HTML 본문 builder()는")
    class Describe_html_builder {

        @Test
        @DisplayName("body가 HTML 문자열 TextNode로 직렬화된다")
        void it_serializes_html_body_as_text_node() {
            // given
            final String html = "<h1>결제 완료</h1><p>49,000원 결제가 승인되었습니다.</p>";
            final JsonNode htmlBody = TextNode.valueOf(html);

            // when
            final JsonNode payload = EmailPayload.builder("제목", htmlBody, "user@example.com")
                    .metadata("bodyType", "HTML")
                    .build();

            // then
            assertThat(payload.get("body").asText()).isEqualTo(html);
            assertThat(payload.get("metadata").get("bodyType").asText()).isEqualTo("HTML");
        }
    }

    @Nested
    @DisplayName("구조화된 본문 builder()는")
    class Describe_structured_builder {

        @Test
        @DisplayName("body가 ObjectNode로 직렬화된다")
        void it_serializes_structured_body_as_object_node() {
            // given
            final String headline = "결제가 완료됐습니다";
            final String cta = "영수증 보기";
            final ObjectNode card = JsonNodeFactory.instance.objectNode()
                    .put("headline", headline)
                    .put("cta", cta);

            // when
            final JsonNode payload = EmailPayload.builder("제목", card, "user@example.com")
                    .metadata("bodyType", "CARD")
                    .build();

            // then
            final JsonNode body = payload.get("body");
            assertThat(body.get("headline").asText()).isEqualTo(headline);
            assertThat(body.get("cta").asText()).isEqualTo(cta);
            assertThat(payload.get("metadata").get("bodyType").asText()).isEqualTo("CARD");
        }
    }

    @Nested
    @DisplayName("EMAIL 최소 계약은")
    class Describe_minimum_contract {

        @Test
        @DisplayName("subject, body, metadata.recipientEmail을 모두 포함한다")
        void it_satisfies_email_minimum_contract() {
            // given & when
            final JsonNode payload = EmailPayload.builder("제목", "본문", "user@example.com").build();

            // then
            assertThat(payload.has("subject")).isTrue();
            assertThat(payload.has("body")).isTrue();
            assertThat(payload.get("metadata").has("recipientEmail")).isTrue();
        }
    }

    @Nested
    @DisplayName("builder()는")
    class Describe_builder_validation {

        @Test
        @DisplayName("subject가 null이면 NPE를 던진다")
        void it_throws_when_subject_is_null() {
            // given
            final String nullSubject = null;

            // when & then
            assertThatNullPointerException()
                    .isThrownBy(() -> EmailPayload.builder(nullSubject, "본문", "user@example.com"))
                    .withMessageContaining("subject");
        }

        @Test
        @DisplayName("body(String)가 null이면 NPE를 던진다")
        void it_throws_when_string_body_is_null() {
            // given
            final String nullBody = null;

            // when & then
            assertThatNullPointerException()
                    .isThrownBy(() -> EmailPayload.builder("제목", nullBody, "user@example.com"))
                    .withMessageContaining("body");
        }

        @Test
        @DisplayName("body(JsonNode)가 null이면 NPE를 던진다")
        void it_throws_when_json_body_is_null() {
            // given
            final JsonNode nullBody = null;

            // when & then
            assertThatNullPointerException()
                    .isThrownBy(() -> EmailPayload.builder("제목", nullBody, "user@example.com"))
                    .withMessageContaining("body");
        }

        @Test
        @DisplayName("recipientEmail이 null이면 NPE를 던진다")
        void it_throws_when_recipient_email_is_null() {
            // given
            final String nullEmail = null;

            // when & then
            assertThatNullPointerException()
                    .isThrownBy(() -> EmailPayload.builder("제목", "본문", nullEmail))
                    .withMessageContaining("recipientEmail");
        }
    }
}
