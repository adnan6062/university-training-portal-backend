package com.university.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Circuit-breaker fallback endpoints.
 * When a downstream service is unreachable the gateway forwards here
 * instead of returning a raw 500.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/identity")
    public ResponseEntity<Map<String, Object>> identityFallback() {
        return fallback("identity-service");
    }

    @RequestMapping("/catalog")
    public ResponseEntity<Map<String, Object>> catalogFallback() {
        return fallback("catalog-service");
    }

    @RequestMapping("/enrollment")
    public ResponseEntity<Map<String, Object>> enrollmentFallback() {
        return fallback("enrollment-service");
    }

    @RequestMapping("/assessment")
    public ResponseEntity<Map<String, Object>> assessmentFallback() {
        return fallback("assessment-service");
    }

    @RequestMapping("/communication")
    public ResponseEntity<Map<String, Object>> communicationFallback() {
        return fallback("communication-service");
    }

    private ResponseEntity<Map<String, Object>> fallback(String service) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error",     service + " is currently unavailable",
                        "message",   "Please try again later",
                        "timestamp", Instant.now().toString()
                ));
    }
}
