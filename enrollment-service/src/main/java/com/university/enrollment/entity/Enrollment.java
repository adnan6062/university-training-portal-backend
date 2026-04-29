package com.university.enrollment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();

    private LocalDateTime activatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private Invoice invoice;

    @PreUpdate
    public void preUpdate() { updatedAt = LocalDateTime.now(); }
}
