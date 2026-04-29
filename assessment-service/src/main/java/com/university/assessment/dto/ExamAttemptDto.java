package com.university.assessment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamAttemptDto {
    private Long id;
    private Long examId;
    private Long studentId;
    private String studentName;
    private Double score;
    private String answers;
    private String feedback;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
}
