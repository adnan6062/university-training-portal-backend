package com.university.assessment.client;

import com.university.assessment.client.dto.EnrollmentResponse;
import com.university.assessment.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "enrollment-service", configuration = FeignClientConfig.class)
public interface EnrollmentServiceClient {

    @GetMapping("/enrollments/student/{studentId}/session/{sessionId}")
    EnrollmentResponse getEnrollmentByStudentAndSession(
            @PathVariable("studentId") Long studentId,
            @PathVariable("sessionId") Long sessionId);
}
