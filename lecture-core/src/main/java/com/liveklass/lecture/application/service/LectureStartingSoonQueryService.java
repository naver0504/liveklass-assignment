package com.liveklass.lecture.application.service;

import com.liveklass.lecture.application.repository.EnrollmentRepository;
import com.liveklass.lecture.domain.event.LectureStartingSoonEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureStartingSoonQueryService {

    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public List<LectureStartingSoonEvent> findEventsFor(
            final LocalDate targetDate,
            final LocalDateTime publishedAt
    ) {
        final LocalDateTime from = targetDate.atStartOfDay();
        final LocalDateTime to = from.plusDays(1).minusSeconds(1);

        return enrollmentRepository.findActiveWithLectureStartingBetween(from, to)
                .stream()
                .map(s -> new LectureStartingSoonEvent(
                        s.lectureId(),
                        s.userId(),
                        s.lectureTitle(),
                        s.lectureStartAt(),
                        s.userId(),
                        String.valueOf(s.enrollmentId()),
                        publishedAt
                ))
                .toList();
    }
}
