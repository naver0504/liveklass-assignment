package com.liveklass.lecture.infrastructure.mapper;

import com.liveklass.lecture.domain.Enrollment;
import com.liveklass.lecture.domain.id.EnrollmentId;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.domain.id.UserId;
import com.liveklass.lecture.infrastructure.persistence.entity.EnrollmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentEntityMapper {

    public Enrollment toDomain(final EnrollmentJpaEntity entity) {
        return new Enrollment(
                new EnrollmentId(entity.getId()),
                new LectureId(entity.getLectureId()),
                new UserId(entity.getUserId()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public EnrollmentJpaEntity toEntity(final Enrollment enrollment) {
        return new EnrollmentJpaEntity(
                enrollment.id() != null ? enrollment.id().value() : null,
                enrollment.lectureId().value(),
                enrollment.userId().value(),
                enrollment.status(),
                enrollment.createdAt(),
                enrollment.updatedAt()
        );
    }
}
