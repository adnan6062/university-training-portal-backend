package com.university.assessment.repository;

import com.university.assessment.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findBySessionId(Long sessionId);
    List<Exam> findByStatus(String status);
}

