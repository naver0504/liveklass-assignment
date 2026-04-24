package com.liveklass.lecture.application.repository;

import com.liveklass.lecture.application.dto.LectureEnrollmentSummary;
import com.liveklass.lecture.domain.Enrollment;
import com.liveklass.lecture.domain.id.EnrollmentId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {

    Enrollment save(Enrollment enrollment);

    Optional<Enrollment> findById(EnrollmentId id);

    List<LectureEnrollmentSummary> findActiveWithLectureStartingBetween(LocalDateTime from, LocalDateTime to);
}
