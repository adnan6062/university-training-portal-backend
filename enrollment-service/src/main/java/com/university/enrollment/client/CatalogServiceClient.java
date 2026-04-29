package com.university.enrollment.client;

import com.university.enrollment.client.dto.SessionResponse;
import com.university.enrollment.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "catalog-service", configuration = FeignClientConfig.class)
public interface CatalogServiceClient {

    @GetMapping("/sessions/{id}")
    SessionResponse getSessionById(@PathVariable("id") Long id);

    @PostMapping("/sessions/{id}/increment-enrollment")
    SessionResponse incrementEnrollment(@PathVariable("id") Long id);

    @PostMapping("/sessions/{id}/decrement-enrollment")
    SessionResponse decrementEnrollment(@PathVariable("id") Long id);
}
