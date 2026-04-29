package com.university.assessment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;
    private Double score;
    private String answers;
    private String feedback;

    @Column(nullable = false)
    private String status = "IN_PROGRESS";

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime submittedAt;
    private Long gradedBy;
    private LocalDateTime gradedAt;
}
