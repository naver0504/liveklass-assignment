package com.liveklass.lecture.infrastructure.repository;

import com.liveklass.lecture.application.repository.LectureRepository;
import com.liveklass.lecture.domain.Lecture;
import com.liveklass.lecture.domain.id.LectureId;
import com.liveklass.lecture.infrastructure.mapper.LectureEntityMapper;
import com.liveklass.lecture.infrastructure.persistence.jpa.JpaLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {

    private final JpaLectureRepository jpaLectureRepository;
    private final LectureEntityMapper mapper;

    @Override
    public Optional<Lecture> findById(final LectureId id) {
        return jpaLectureRepository.findById(id.value())
                .map(mapper::toDomain);
    }
}
