package com.university.assessment.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentResponse {
    private Long id;
    private Long sessionId;
    private Long studentId;
    private String studentName;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime activatedAt;
    private LocalDateTime completedAt;
}
