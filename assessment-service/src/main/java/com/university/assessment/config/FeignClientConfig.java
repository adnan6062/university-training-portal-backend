package com.university.assessment.config;

import com.university.assessment.exception.ResourceNotFoundException;
import com.university.assessment.exception.ServiceCommunicationException;
import com.university.assessment.exception.StudentNotEnrolledException;
import feign.codec.ErrorDecoder;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

// NOTE: intentionally NOT annotated with @Configuration so this class is only
// registered inside each Feign client's child context (via configuration = FeignClientConfig.class).
// Adding @Configuration here would cause duplicate bean registration in the parent Spring context.
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String message = "Service communication error";
            try {
                if (response.body() != null) {
                    message = new String(response.body().asInputStream().readAllBytes());
                }
            } catch (IOException ignored) {}

            return switch (response.status()) {
                case 404 -> new ResourceNotFoundException(
                        extractMessage(message, "Resource not found"));
                case 403 -> new StudentNotEnrolledException(
                        extractMessage(message, "Student is not enrolled in this session"));
                case 400 -> new ServiceCommunicationException(
                        extractMessage(message, "Bad request to downstream service"));
                default -> new ServiceCommunicationException(
                        "Downstream service returned " + response.status() + ": " + extractMessage(message, ""));
            };
        };
    }

    private String extractMessage(String raw, String fallback) {
        if (raw == null || raw.isBlank()) return fallback;
        int idx = raw.indexOf("\"error\"");
        if (idx >= 0) {
            int start = raw.indexOf('"', idx + 7) + 1;
            int end   = raw.indexOf('"', start);
            if (start > 0 && end > start) return raw.substring(start, end);
        }
        return raw.length() > 200 ? raw.substring(0, 200) : raw;
    }
}
