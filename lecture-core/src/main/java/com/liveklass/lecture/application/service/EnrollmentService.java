package com.liveklass.lecture.application.service;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.lecture.application.repository.EnrollmentRepository;
import com.liveklass.lecture.application.repository.LectureRepository;
import com.liveklass.lecture.domain.Enrollment;
import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.event.EnrollmentCancelledEvent;
import com.liveklass.lecture.domain.event.EnrollmentCompletedEvent;
import com.liveklass.lecture.domain.exception.EnrollmentException;
import com.liveklass.lecture.domain.exception.LectureException;
import com.liveklass.lecture.domain.id.EnrollmentId;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.domain.id.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public void enroll(final Long lectureId, final Long userId, final LocalDateTime now) {
        final Lecture lecture = lectureRepository.findById(new LectureId(lectureId))
                .orElseThrow(() -> ExceptionCreator.create(LectureException.LECTURE_NOT_FOUND,
                        "lectureId: " + lectureId));

        final Enrollment enrollment = enrollmentRepository.save(
                Enrollment.create(new LectureId(lectureId), new UserId(userId), now)
        );

        eventPublisher.publish(new EnrollmentCompletedEvent(
                enrollment.id().value(),
                lectureId,
                userId,
                lecture.title(),
                userId,
                String.valueOf(enrollment.id().value()),
                now
        ));
    }

    @Transactional
    public void cancel(
            final Long enrollmentId,
            final Long userId,
            final String cancelReason,
            final LocalDateTime now
    ) {
        final Enrollment enrollment = enrollmentRepository.findById(new EnrollmentId(enrollmentId))
                .orElseThrow(() -> ExceptionCreator.create(EnrollmentException.ENROLLMENT_NOT_FOUND,
                        "enrollmentId: " + enrollmentId));

        if (!enrollment.userId().value().equals(userId)) {
            throw ExceptionCreator.create(EnrollmentException.ENROLLMENT_OWNER_MISMATCH,
                    "userId: " + userId);
        }

        final Lecture lecture = lectureRepository.findById(enrollment.lectureId())
                .orElseThrow(() -> ExceptionCreator.create(LectureException.LECTURE_NOT_FOUND,
                        "lectureId: " + enrollment.lectureId().value()));

        final Enrollment cancelled = enrollment.cancel(now);
        enrollmentRepository.save(cancelled);

        eventPublisher.publish(new EnrollmentCancelledEvent(
                enrollmentId,
                enrollment.lectureId().value(),
                userId,
                lecture.title(),
                cancelReason,
                userId,
                String.valueOf(enrollmentId),
                now
        ));
    }
}
