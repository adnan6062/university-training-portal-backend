package com.university.communication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {
    private Long recipientId;
    @NotBlank @Email
    private String recipientEmail;
    private String recipientName;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
    private String type = "EMAIL";
    private Long templateId;
    private Long relatedEntityId;
    private String relatedEntityType;
}
