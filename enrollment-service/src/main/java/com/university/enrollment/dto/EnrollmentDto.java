package com.university.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {
    private Long id;
    private Long sessionId;
    private Long studentId;
    private String studentName;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime activatedAt;
    private LocalDateTime completedAt;
    private InvoiceDto invoice;
}
