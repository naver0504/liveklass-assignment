package com.liveklass.lecture.domain.id;

import java.util.Objects;

public record UserId(Long value) {
    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }
}
