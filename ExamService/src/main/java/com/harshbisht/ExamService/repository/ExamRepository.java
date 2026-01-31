package com.harshbisht.ExamService.repository;

import com.harshbisht.ExamService.entity.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {
}
