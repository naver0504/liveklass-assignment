package com.liveklass.lecture.infrastructure.persistence.jpa;

import com.liveklass.lecture.infrastructure.persistence.entity.LectureJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLectureRepository extends JpaRepository<LectureJpaEntity, Long> {}
