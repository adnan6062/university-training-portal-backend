package com.university.assessment.repository;

import com.university.assessment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findBySessionId(Long sessionId);
    List<Assignment> findByStatus(String status);
}
