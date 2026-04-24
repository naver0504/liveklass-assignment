package com.liveklass.lecture.infrastructure.persistence.jpa;

import com.liveklass.lecture.application.dto.LectureEnrollmentSummary;
import com.liveklass.lecture.infrastructure.persistence.entity.EnrollmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaEnrollmentRepository extends JpaRepository<EnrollmentJpaEntity, Long> {

    Optional<EnrollmentJpaEntity> findByLectureIdAndUserId(Long lectureId, Long userId);

    @Query("""
            SELECT new com.liveklass.lecture.application.dto.LectureEnrollmentSummary(
                l.id, l.title, l.startAt, e.id, e.userId)
            FROM EnrollmentJpaEntity e
            JOIN LectureJpaEntity l ON e.lectureId = l.id
            WHERE e.status = 'ENROLLED'
              AND l.startAt >= :from
              AND l.startAt <= :to
            """)
    List<LectureEnrollmentSummary> findActiveWithLectureStartingBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
