package com.university.communication.client;

import com.university.communication.client.dto.GradeResponse;
import com.university.communication.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "assessment-service", configuration = FeignClientConfig.class)
public interface AssessmentServiceClient {

    @GetMapping("/grades/student/{studentId}/session/{sessionId}")
    GradeResponse getGradeByStudentAndSession(
            @PathVariable("studentId") Long studentId,
            @PathVariable("sessionId") Long sessionId);
}
