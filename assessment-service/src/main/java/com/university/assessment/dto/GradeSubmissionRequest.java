package com.university.assessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeSubmissionRequest {
    @NotNull private Double score;
    private String feedback;
    private Long gradedBy;
}
