package com.harshbisht.ExamService.repository;

import com.harshbisht.ExamService.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByExamId(Long examId);
}
