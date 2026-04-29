package com.university.catalog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SyllabusDto {
    private Long id;
    private Long courseId;
    private String content;
    private String objectives;
    private String prerequisites;
    private String textbooks;
    private LocalDateTime createdAt;
}
