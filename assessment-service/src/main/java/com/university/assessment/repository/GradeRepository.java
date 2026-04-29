package com.university.assessment.repository;

import com.university.assessment.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findBySessionId(Long sessionId);
    Optional<Grade> findByStudentIdAndSessionId(Long studentId, Long sessionId);
}
