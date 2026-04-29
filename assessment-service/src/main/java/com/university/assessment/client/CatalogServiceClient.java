package com.university.assessment.client;

import com.university.assessment.client.dto.SessionResponse;
import com.university.assessment.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", configuration = FeignClientConfig.class)
public interface CatalogServiceClient {

    @GetMapping("/sessions/{id}")
    SessionResponse getSessionById(@PathVariable("id") Long id);
}
