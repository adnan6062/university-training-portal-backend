package com.university.communication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IssueCertificateRequest {
    @NotNull private Long studentId;
    private String studentName;
    private String studentEmail;
    @NotNull private Long courseId;
    private String courseTitle;
    private Double finalGrade;
    private String letterGrade;
    private String notes;
}
