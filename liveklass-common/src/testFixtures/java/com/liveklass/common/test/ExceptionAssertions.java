package com.liveklass.common.test;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.error.ExceptionInterface;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExceptionAssertions {

    private ExceptionAssertions() {}

    public static void assertThatExceptionOfType(
            final ThrowableAssert.ThrowingCallable callable,
            final ExceptionInterface expectedException
    ) {
        assertThatThrownBy(callable)
                .isInstanceOf(ExceptionCreator.create(expectedException).getClass())
                .hasMessage(expectedException.getMessage());
    }
}
