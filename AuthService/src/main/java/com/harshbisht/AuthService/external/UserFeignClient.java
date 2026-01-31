package com.harshbisht.AuthService.external;

import com.harshbisht.AuthService.config.FeignConfig;
import com.harshbisht.AuthService.dto.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE", configuration = FeignConfig.class)
public interface UserFeignClient {
    @PostMapping("/users")
    UserEntity createUser(@RequestBody UserEntity user);
}
