package com.liveklass.lecture.application.repository;

import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.id.LectureId;

import java.util.Optional;

public interface LectureRepository {

    Optional<Lecture> findById(LectureId id);
}
