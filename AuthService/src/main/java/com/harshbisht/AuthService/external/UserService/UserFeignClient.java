package com.harshbisht.AuthService.external.UserService;

import com.harshbisht.AuthService.dto.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserFeignClient {

    @PostMapping("/users")
    UserEntity createUser(
            @RequestBody UserEntity user,
            @RequestHeader("X-INTERNAL-KEY") String key
    );
}