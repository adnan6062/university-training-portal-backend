package com.university.assessment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String fileUrl;
    private Double score;
    private String feedback;

    @Column(nullable = false)
    private String status = "SUBMITTED";

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime gradedAt;
    private Long gradedBy;
}
