package com.university.catalog.repository;

import com.university.catalog.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByCourseId(Long courseId);
    List<Session> findByStatus(String status);
    List<Session> findByCourseIdAndStatus(Long courseId, String status);
}
