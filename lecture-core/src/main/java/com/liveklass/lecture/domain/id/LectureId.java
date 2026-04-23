package com.liveklass.lecture.domain.id;

import java.util.Objects;

public record LectureId(Long value) {
    public LectureId {
        Objects.requireNonNull(value, "value must not be null");
    }
}
