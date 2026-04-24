package com.liveklass.lecture.application.service;

import com.liveklass.lecture.application.repository.LectureRepository;
import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.id.LectureId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    @Transactional
    public Lecture create(final String title, final LocalDateTime startAt) {
        final LocalDateTime now = LocalDateTime.now();
        return lectureRepository.save(new Lecture(null, title, startAt, now, now));
    }
}
