package com.university.assessment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubmissionDto {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String content;
    private String fileUrl;
    private Double score;
    private String feedback;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
}
