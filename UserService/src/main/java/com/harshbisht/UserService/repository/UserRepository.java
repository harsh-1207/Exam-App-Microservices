package com.harshbisht.UserService.repository;

import com.harshbisht.UserService.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
