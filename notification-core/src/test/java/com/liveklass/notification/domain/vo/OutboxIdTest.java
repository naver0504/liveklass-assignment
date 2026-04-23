package com.liveklass.notification.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Tag("UNIT_TEST")
@DisplayName("OutboxId는")
class OutboxIdTest {

    @Nested
    @DisplayName("of()는")
    class Describe_of {

        @Test
        @DisplayName("양수 id를 그대로 보유한다")
        void it_holds_positive_id() {
            // given
            final Long id = 42L;

            // when
            final OutboxId outboxId = OutboxId.of(id);

            // then
            assertThat(outboxId.id()).isEqualTo(id);
        }

        @Test
        @DisplayName("null id는 허용한다 (DB 저장 전 미할당 상태)")
        void it_allows_null_id() {
            // given & when
            final OutboxId outboxId = OutboxId.of(null);

            // then
            assertThat(outboxId.id()).isNull();
        }

        @Test
        @DisplayName("0 이하 id는 예외를 던진다")
        void it_throws_when_id_is_non_positive() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> OutboxId.of(0L));
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> OutboxId.of(-1L));
        }
    }
}
