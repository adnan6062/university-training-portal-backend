package com.university.communication.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationLogDto {
    private Long id;
    private Long recipientId;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String body;
    private String type;
    private String status;
    private String errorMessage;
    private Long templateId;
    private Long relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
