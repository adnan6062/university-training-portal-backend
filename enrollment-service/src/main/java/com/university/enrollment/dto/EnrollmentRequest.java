package com.university.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EnrollmentRequest {
    @NotNull private Long studentId;
    private String studentName;
    private BigDecimal amount;
    private String description;
}
