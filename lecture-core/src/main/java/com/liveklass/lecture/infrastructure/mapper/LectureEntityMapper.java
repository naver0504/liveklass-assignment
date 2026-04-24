package com.liveklass.lecture.infrastructure.mapper;

import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.infrastructure.persistence.entity.LectureJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LectureEntityMapper {

    public Lecture toDomain(final LectureJpaEntity entity) {
        return new Lecture(
                new LectureId(entity.getId()),
                entity.getTitle(),
                entity.getStartAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public LectureJpaEntity toEntity(final Lecture lecture) {
        return new LectureJpaEntity(
                lecture.id() != null ? lecture.id().value() : null,
                lecture.title(),
                lecture.startAt(),
                lecture.createdAt(),
                lecture.updatedAt()
        );
    }
}
