package com.liveklass.lecture.domain.id;

import java.util.Objects;

public record EnrollmentId(Long value) {
    public EnrollmentId {
        Objects.requireNonNull(value, "value must not be null");
    }
}
