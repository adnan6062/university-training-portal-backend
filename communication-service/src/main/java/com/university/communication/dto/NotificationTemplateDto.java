package com.university.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationTemplateDto {
    private Long id;
    @NotBlank private String name;
    @NotBlank private String subject;
    @NotBlank private String body;
    private String type;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
}
