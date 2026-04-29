package com.university.communication.service;

import com.university.communication.entity.NotificationLog;
import com.university.communication.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;

    @Async
    public void sendEmailAsync(NotificationLog notificationLog) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notificationLog.getRecipientEmail());
            message.setSubject(notificationLog.getSubject());
            message.setText(notificationLog.getBody());
            message.setFrom("2200090035csit@gmail.com");

            mailSender.send(message);

            notificationLog.setStatus("SENT");
            notificationLog.setSentAt(LocalDateTime.now());

            log.info("EMAIL SENT → {}", notificationLog.getRecipientEmail());

        } catch (Exception e) {
            notificationLog.setStatus("FAILED");
            notificationLog.setErrorMessage(e.getMessage());

            log.error("EMAIL FAILED → {}", notificationLog.getRecipientEmail(), e);
        }

        notificationLogRepository.save(notificationLog);
    }
}
