package com.harshbisht.WebService.external;

import com.harshbisht.WebService.dto.AuthResponse;
import com.harshbisht.WebService.dto.LoginRequest;
import com.harshbisht.WebService.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthFeignClient {
    @PostMapping("/auth/register")  // use /auth/** so Gateway routes correctly
    String register(@RequestBody RegisterRequest req);

    @PostMapping("/auth/login")
    AuthResponse login(@RequestBody LoginRequest req);
}