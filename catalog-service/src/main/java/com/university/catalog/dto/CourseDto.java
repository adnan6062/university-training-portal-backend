package com.university.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseDto {
    private Long id;
    @NotBlank private String code;
    @NotBlank private String title;
    private String description;
    private String category;
    private String level;
    private Integer creditHours;
    private String status;
    private LocalDateTime createdAt;
}
