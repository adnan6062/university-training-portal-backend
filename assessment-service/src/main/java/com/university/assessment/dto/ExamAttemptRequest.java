package com.university.assessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamAttemptRequest {
    @NotNull private Long studentId;
    private String studentName;
    private String answers;
}
