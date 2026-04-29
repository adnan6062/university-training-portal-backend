package com.university.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SessionDto {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull private Integer capacity;
    private Integer enrolledCount;
    private String mode;
    private String instructor;
    private String location;
    private String status;
    private LocalDateTime createdAt;
}
