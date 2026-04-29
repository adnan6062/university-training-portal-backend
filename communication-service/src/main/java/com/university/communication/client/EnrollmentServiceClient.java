package com.university.communication.client;

import com.university.communication.client.dto.EnrollmentResponse;
import com.university.communication.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "enrollment-service", configuration = FeignClientConfig.class)
public interface EnrollmentServiceClient {

    @GetMapping("/enrollments/{id}")
    EnrollmentResponse getEnrollmentById(@PathVariable("id") Long id);
}
