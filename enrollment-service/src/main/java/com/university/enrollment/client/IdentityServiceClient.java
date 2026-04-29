package com.university.enrollment.client;

import com.university.enrollment.client.dto.UserResponse;
import com.university.enrollment.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", configuration = FeignClientConfig.class)
public interface IdentityServiceClient {

    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
