package com.liveklass.lecture.application.repository;

import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.id.LectureId;

import java.util.Optional;

public interface LectureRepository {

    Lecture save(Lecture lecture);

    Optional<Lecture> findById(LectureId id);
}
