package com.university.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamDto {
    private Long id;
    private Long sessionId;
    @NotBlank private String title;
    private String description;
    @NotNull private LocalDateTime startAt;
    @NotNull private Integer duration;
    private Double totalMarks;
    private Double passingMarks;
    private String status;
    private LocalDateTime createdAt;
}
