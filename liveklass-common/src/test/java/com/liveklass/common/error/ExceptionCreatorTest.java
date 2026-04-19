package com.liveklass.common.error;

import com.liveklass.common.error.exception.AbstractException;
import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.common.error.exception.ConflictException;
import com.liveklass.common.error.exception.ForbiddenException;
import com.liveklass.common.error.exception.InternalServerErrorException;
import com.liveklass.common.error.exception.NotFoundException;
import com.liveklass.common.error.exception.ServiceUnavailableException;
import com.liveklass.common.error.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UNIT_TEST")
@DisplayName("ExceptionCreator 단위 테스트")
class ExceptionCreatorTest {

    enum TestError implements ExceptionInterface {
        BAD_REQUEST("E400", "잘못된 요청", BadRequestException.class),
        UNAUTHORIZED("E401", "인증 필요", UnauthorizedException.class),
        FORBIDDEN("E403", "접근 불가", ForbiddenException.class),
        NOT_FOUND("E404", "찾을 수 없음", NotFoundException.class),
        CONFLICT("E409", "충돌 발생", ConflictException.class),
        INTERNAL_SERVER_ERROR("E500", "서버 오류", InternalServerErrorException.class),
        SERVICE_UNAVAILABLE("E503", "서비스 불가", ServiceUnavailableException.class);

        private final String errorCode;
        private final String message;
        private final Class<?> aClass;

        TestError(String errorCode, String message, Class<?> aClass) {
            this.errorCode = errorCode;
            this.message = message;
            this.aClass = aClass;
        }

        @Override public String getErrorCode() { return errorCode; }
        @Override public String getMessage() { return message; }
        @Override public Class<?> getAClass() { return aClass; }
    }

    @Nested
    @DisplayName("create(ExceptionInterface) 메서드는")
    class Describe_create {

        @Nested
        @DisplayName("errorLog 없이 호출하면")
        class Context_without_errorLog {

            @Test
            @DisplayName("errorCode·message가 설정되고 errorLog는 null이다")
            void it_creates_exception_without_errorLog() {
                // given
                final TestError error = TestError.BAD_REQUEST;

                // when
                final AbstractException exception = ExceptionCreator.create(error);

                // then
                assertThat(exception).isInstanceOf(BadRequestException.class);
                assertThat(exception.getErrorCode()).isEqualTo(error.getErrorCode());
                assertThat(exception.getMessage()).isEqualTo(error.getMessage());
                assertThat(exception.getErrorLog()).isNull();
            }

            @ParameterizedTest(name = "{0} → {1}")
            @MethodSource("com.liveklass.common.error.ExceptionCreatorTest#exceptionTypeSource")
            @DisplayName("각 ExceptionInterface 구현체에서 올바른 예외 타입과 필드값이 생성된다")
            void it_creates_correct_exception_type(
                    final TestError error,
                    final Class<? extends AbstractException> expectedType) {
                // when
                final AbstractException exception = ExceptionCreator.create(error);

                // then
                assertThat(exception).isInstanceOf(expectedType);
                assertThat(exception.getErrorCode()).isEqualTo(error.getErrorCode());
                assertThat(exception.getMessage()).isEqualTo(error.getMessage());
            }
        }

        @Nested
        @DisplayName("errorLog와 함께 호출하면")
        class Context_with_errorLog {

            @Test
            @DisplayName("errorLog가 예외에 설정된다")
            void it_creates_exception_with_errorLog() {
                // given
                final TestError error = TestError.NOT_FOUND;
                final String errorLog = "상세 디버그 로그";

                // when
                final AbstractException exception = ExceptionCreator.create(error, errorLog);

                // then
                assertThat(exception).isInstanceOf(NotFoundException.class);
                assertThat(exception.getErrorCode()).isEqualTo(error.getErrorCode());
                assertThat(exception.getMessage()).isEqualTo(error.getMessage());
                assertThat(exception.getErrorLog()).isEqualTo(errorLog);
            }
        }
    }

    static Stream<Arguments> exceptionTypeSource() {
        return Stream.of(
                Arguments.of(TestError.BAD_REQUEST, BadRequestException.class),
                Arguments.of(TestError.UNAUTHORIZED, UnauthorizedException.class),
                Arguments.of(TestError.FORBIDDEN, ForbiddenException.class),
                Arguments.of(TestError.NOT_FOUND, NotFoundException.class),
                Arguments.of(TestError.CONFLICT, ConflictException.class),
                Arguments.of(TestError.INTERNAL_SERVER_ERROR, InternalServerErrorException.class),
                Arguments.of(TestError.SERVICE_UNAVAILABLE, ServiceUnavailableException.class)
        );
    }
}
