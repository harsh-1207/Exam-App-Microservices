package com.harshbisht.ExamService.repository;

import com.harshbisht.ExamService.entity.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
}
