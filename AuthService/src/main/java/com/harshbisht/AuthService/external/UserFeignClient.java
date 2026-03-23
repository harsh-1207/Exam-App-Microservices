package com.harshbisht.AuthService.external;

import com.harshbisht.AuthService.config.FeignConfig;
import com.harshbisht.AuthService.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * FIX: Was importing com.harshbisht.AuthService.dto.UserEntity — a stale DTO
 * with only id + name. Switched to UserDto which includes email and role,
 * matching the updated UserService POST /users contract.
 */
@FeignClient(name = "USER-SERVICE", configuration = FeignConfig.class)
public interface UserFeignClient {

    @PostMapping("/users")
    UserDto createUser(@RequestBody UserDto user);
}