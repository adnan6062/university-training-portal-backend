package com.university.communication.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CertificateDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseTitle;
    private Long enrollmentId;
    private String certificateNumber;
    private String status;
    private Double finalGrade;
    private String letterGrade;
    private LocalDateTime issuedAt;
    private String notes;
}
