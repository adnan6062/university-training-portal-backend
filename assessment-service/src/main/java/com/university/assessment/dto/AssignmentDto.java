package com.university.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private Long id;
    private Long sessionId;
    @NotBlank private String title;
    private String description;
    @NotNull private LocalDateTime dueDate;
    private Double maxScore;
    private String status;
    private LocalDateTime createdAt;
}
