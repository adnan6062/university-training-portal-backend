package com.university.communication.service;

import com.university.communication.dto.NotificationLogDto;
import com.university.communication.dto.NotificationRequest;
import com.university.communication.dto.NotificationTemplateDto;
import com.university.communication.entity.NotificationLog;
import com.university.communication.entity.NotificationTemplate;
import com.university.communication.repository.NotificationLogRepository;
import com.university.communication.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailSenderService emailSenderService;

    // ==========================
    // SEND NOTIFICATION (EMAIL)
    // ==========================
    @Transactional
    public NotificationLogDto sendNotification(NotificationRequest request) {

        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setRecipientId(request.getRecipientId());
        notificationLog.setRecipientEmail(request.getRecipientEmail());
        notificationLog.setRecipientName(request.getRecipientName());
        notificationLog.setSubject(request.getSubject());
        notificationLog.setBody(request.getBody());
        notificationLog.setType(request.getType() != null ? request.getType() : "EMAIL");
        notificationLog.setTemplateId(request.getTemplateId());
        notificationLog.setRelatedEntityId(request.getRelatedEntityId());
        notificationLog.setRelatedEntityType(request.getRelatedEntityType());
        notificationLog.setStatus("PENDING");

        NotificationLog savedLog = notificationLogRepository.save(notificationLog);

        // ✅ Async email sending (non-blocking) — called via separate bean so @Async proxy fires
        emailSenderService.sendEmailAsync(savedLog);

        return toDto(savedLog);
    }

    // ==========================
    // NOTIFICATION LOG QUERIES
    // ==========================
    public List<NotificationLogDto> getAllNotifications() {
        return notificationLogRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public NotificationLogDto getNotificationById(Long id) {
        return notificationLogRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found with id: " + id));
    }

    public List<NotificationLogDto> getNotificationsByRecipient(Long recipientId) {
        return notificationLogRepository.findByRecipientId(recipientId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<NotificationLogDto> getNotificationsByEmail(String email) {
        return notificationLogRepository.findByRecipientEmail(email)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ==========================
    // TEMPLATE CRUD OPERATIONS
    // ==========================
    

    public List<NotificationTemplateDto> getAllTemplates() {
        return templateRepository.findAll()
                .stream()
                .map(this::toTemplateDto)
                .toList();
    }

    public NotificationTemplateDto getTemplateById(Long id) {
        return templateRepository.findById(id)
                .map(this::toTemplateDto)
                .orElseThrow(() ->
                        new RuntimeException("Template not found with id: " + id));
    }

    @Transactional
    public NotificationTemplateDto createTemplate(NotificationTemplateDto dto) {
        NotificationTemplate template = new NotificationTemplate();
        template.setName(dto.getName());
        template.setSubject(dto.getSubject());
        template.setBody(dto.getBody());
        template.setType(dto.getType() != null ? dto.getType() : "EMAIL");
        template.setDescription(dto.getDescription());
        template.setActive(true);

        return toTemplateDto(templateRepository.save(template));
    }

    @Transactional
    public NotificationTemplateDto updateTemplate(Long id, NotificationTemplateDto dto) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Template not found with id: " + id));

        template.setSubject(dto.getSubject());
        template.setBody(dto.getBody());
        template.setDescription(dto.getDescription());
        template.setActive(dto.isActive());

        return toTemplateDto(templateRepository.save(template));
    }

    @Transactional
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }

    // ==========================
    // DTO MAPPERS
    // ==========================

    private NotificationLogDto toDto(NotificationLog n) {
        NotificationLogDto dto = new NotificationLogDto();
        dto.setId(n.getId());
        dto.setRecipientId(n.getRecipientId());
        dto.setRecipientEmail(n.getRecipientEmail());
        dto.setRecipientName(n.getRecipientName());
        dto.setSubject(n.getSubject());
        dto.setBody(n.getBody());
        dto.setType(n.getType());
        dto.setStatus(n.getStatus());
        dto.setErrorMessage(n.getErrorMessage());
        dto.setTemplateId(n.getTemplateId());
        dto.setRelatedEntityId(n.getRelatedEntityId());
        dto.setRelatedEntityType(n.getRelatedEntityType());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setSentAt(n.getSentAt());
        return dto;
    }

    private NotificationTemplateDto toTemplateDto(NotificationTemplate t) {
        NotificationTemplateDto dto = new NotificationTemplateDto();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setSubject(t.getSubject());
        dto.setBody(t.getBody());
        dto.setType(t.getType());
        dto.setDescription(t.getDescription());
        dto.setActive(t.isActive());
        dto.setCreatedAt(t.getCreatedAt());
        return dto;
    }
}
