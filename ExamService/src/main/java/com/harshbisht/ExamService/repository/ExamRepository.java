package com.harshbisht.ExamService.repository;

import com.harshbisht.ExamService.dto.ExamResponse;
import com.harshbisht.ExamService.entity.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {
    List<ExamEntity> findBySubjectId(Long subjectId);
}
