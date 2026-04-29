package com.university.assessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAssignmentRequest {
    @NotNull private Long studentId;
    private String studentName;
    private String content;
    private String fileUrl;
}
