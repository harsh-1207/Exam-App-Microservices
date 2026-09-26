package com.harshbisht.ResultService.repository;

import com.harshbisht.ResultService.entity.ResultEntity;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
	boolean existsByExamIdAndStudentId(Long examId, Long studentId);

	Optional<ResultEntity> findByExamIdAndStudentId(Long examId, Long studentId);

	List<ResultEntity> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

	List<ResultEntity> findByExamIdOrderBySubmittedAtDesc(Long examId);

}
