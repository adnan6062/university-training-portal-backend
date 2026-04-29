package com.university.communication.controller;

import com.university.communication.dto.NotificationLogDto;
import com.university.communication.dto.NotificationRequest;
import com.university.communication.dto.NotificationTemplateDto;
import com.university.communication.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/notify")
    @Operation(summary = "Send a notification")
    public ResponseEntity<NotificationLogDto> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.sendNotification(request));
    }

    @GetMapping("/notifications")
    @Operation(summary = "Get all notifications")
    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    public ResponseEntity<List<NotificationLogDto>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/notifications/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationLogDto> getNotificationById(
            @Parameter(description = "Notification ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/notifications/recipient/{recipientId}")
    @Operation(summary = "Get notifications by recipient")
    public ResponseEntity<List<NotificationLogDto>> getNotificationsByRecipient(
            @Parameter(description = "Recipient (user) ID", required = true, example = "1")
            @PathVariable("recipientId") Long recipientId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipientId));
    }

    @GetMapping("/templates")
    @Operation(summary = "Get all notification templates")
    public ResponseEntity<List<NotificationTemplateDto>> getAllTemplates() {
        return ResponseEntity.ok(notificationService.getAllTemplates());
    }

    @GetMapping("/templates/{id}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<NotificationTemplateDto> getTemplateById(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationService.getTemplateById(id));
    }

    @PostMapping("/templates")
    @Operation(summary = "Create notification template")
    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    public ResponseEntity<NotificationTemplateDto> createTemplate(@Valid @RequestBody NotificationTemplateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createTemplate(dto));
    }

    @PutMapping("/templates/{id}")
    @Operation(summary = "Update notification template")
    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    public ResponseEntity<NotificationTemplateDto> updateTemplate(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody NotificationTemplateDto dto) {
        return ResponseEntity.ok(notificationService.updateTemplate(id, dto));
    }

    @DeleteMapping("/templates/{id}")
    @Operation(summary = "Delete notification template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        notificationService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
