package com.university.communication.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GradeResponse {
    private Long id;
    private Long studentId;
    private Long sessionId;
    private Long enrollmentId;
    private Double assignmentScore;
    private Double examScore;
    private Double finalGrade;
    private String letterGrade;
    private String status;
    private LocalDateTime createdAt;
}
