package com.harshbisht.ExamService.repository;

import com.harshbisht.ExamService.entity.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {
    List<ExamEntity> findBySubject_Id(Long subjectId);

    List<ExamEntity> findByTeacherId(Long teacherId);

    List<ExamEntity> findBySubject_IdAndPublished(Long subjectId, Boolean published);

    List<ExamEntity> findByPublished(Boolean published);
}

