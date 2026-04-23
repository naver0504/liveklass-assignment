package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@Tag("UNIT_TEST")
@DisplayName("InAppPayload 빌더는")
class InAppPayloadTest {

    @Nested
    @DisplayName("build()는")
    class Describe_build {

        @Test
        @DisplayName("title과 body를 JSON 최상위 필드로 반환한다")
        void it_returns_title_and_body_as_root_fields() {
            // given
            final String title = "수강 신청 완료";
            final String body = "Spring Boot 수강 신청이 완료되었습니다.";

            // when
            final JsonNode payload = InAppPayload.builder(title, body).build();

            // then
            assertThat(payload.get("title").asText()).isEqualTo(title);
            assertThat(payload.get("body").asText()).isEqualTo(body);
        }

        @Test
        @DisplayName("metadata 없이 호출하면 빈 metadata 객체를 포함한다")
        void it_includes_empty_metadata_when_none_added() {
            // given
            final String title = "제목";
            final String body = "본문";

            // when
            final JsonNode payload = InAppPayload.builder(title, body).build();

            // then
            assertThat(payload.get("metadata").isObject()).isTrue();
            assertThat(payload.get("metadata").isEmpty()).isTrue();
        }

        @Test
        @DisplayName("metadata() 체이닝으로 추가한 값이 metadata 객체 안에 들어간다")
        void it_puts_metadata_entries_under_metadata_node() {
            // given
            final String screen = "LECTURE_DETAIL";
            final long lectureId = 42L;
            final boolean isNew = true;

            // when
            final JsonNode payload = InAppPayload.builder("제목", "본문")
                    .metadata("screen", screen)
                    .metadata("lectureId", lectureId)
                    .metadata("isNew", isNew)
                    .build();

            // then
            final JsonNode metadata = payload.get("metadata");
            assertThat(metadata.get("screen").asText()).isEqualTo(screen);
            assertThat(metadata.get("lectureId").asLong()).isEqualTo(lectureId);
            assertThat(metadata.get("isNew").asBoolean()).isEqualTo(isNew);
        }

        @Test
        @DisplayName("IN_APP 최소 계약 필드(title, body)를 모두 포함한다")
        void it_satisfies_in_app_minimum_contract() {
            // given & when
            final JsonNode payload = InAppPayload.builder("제목", "본문").build();

            // then
            assertThat(payload.has("title")).isTrue();
            assertThat(payload.has("body")).isTrue();
        }
    }

    @Nested
    @DisplayName("builder()는")
    class Describe_builder {

        @Test
        @DisplayName("title이 null이면 NPE를 던진다")
        void it_throws_when_title_is_null() {
            // given
            final String nullTitle = null;

            // when & then
            assertThatNullPointerException()
                    .isThrownBy(() -> InAppPayload.builder(nullTitle, "본문"))
                    .withMessageContaining("title");
        }

        @Test
        @DisplayName("body가 null이면 NPE를 던진다")
        void it_throws_when_body_is_null() {
            // given
            final String nullBody = null;

            // when & then
            assertThatNullPointerException()
                    .isThrownBy(() -> InAppPayload.builder("제목", nullBody))
                    .withMessageContaining("body");
        }
    }
}
