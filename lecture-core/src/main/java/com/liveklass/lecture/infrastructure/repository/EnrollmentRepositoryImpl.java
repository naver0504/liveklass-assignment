package com.liveklass.lecture.infrastructure.repository;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.lecture.application.dto.LectureEnrollmentSummary;
import com.liveklass.lecture.application.repository.EnrollmentRepository;
import com.liveklass.lecture.domain.Enrollment;
import com.liveklass.lecture.domain.enums.EnrollmentStatus;
import com.liveklass.lecture.domain.exception.EnrollmentException;
import com.liveklass.lecture.domain.id.EnrollmentId;
import com.liveklass.lecture.infrastructure.mapper.EnrollmentEntityMapper;
import com.liveklass.lecture.infrastructure.persistence.jpa.JpaEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final JpaEnrollmentRepository jpaEnrollmentRepository;
    private final EnrollmentEntityMapper mapper;

    @Override
    public Enrollment save(final Enrollment enrollment) {
        jpaEnrollmentRepository.findByLectureIdAndUserId(
                enrollment.lectureId().value(),
                enrollment.userId().value()
        ).ifPresent(existing -> {
            if (existing.getStatus() == EnrollmentStatus.ENROLLED) {
                throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_ALREADY_EXISTS,
                        "lectureId: " + enrollment.lectureId().value() + ", userId: " + enrollment.userId().value());
            }
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_REENROLL_NOT_ALLOWED,
                    "lectureId: " + enrollment.lectureId().value() + ", userId: " + enrollment.userId().value());
        });

        try {
            return mapper.toDomain(jpaEnrollmentRepository.saveAndFlush(mapper.toEntity(enrollment)));
        } catch (DataIntegrityViolationException e) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_ALREADY_EXISTS,
                    "lectureId: " + enrollment.lectureId().value() + ", userId: " + enrollment.userId().value());
        }
    }

    @Override
    public Optional<Enrollment> findById(final EnrollmentId id) {
        return jpaEnrollmentRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<LectureEnrollmentSummary> findActiveWithLectureStartingBetween(
            final LocalDateTime from,
            final LocalDateTime to
    ) {
        return jpaEnrollmentRepository.findActiveWithLectureStartingBetween(from, to);
    }
}
