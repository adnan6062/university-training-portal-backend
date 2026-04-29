package com.university.communication.repository;

import com.university.communication.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipientId(Long recipientId);
    List<NotificationLog> findByStatus(String status);
    List<NotificationLog> findByRecipientEmail(String email);
    List<NotificationLog> findByRelatedEntityIdAndRelatedEntityType(Long relatedEntityId, String relatedEntityType);
}
