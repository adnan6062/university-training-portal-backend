package com.university.communication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;
    private String studentEmail;

    @Column(nullable = false)
    private Long courseId;

    private String courseTitle;

    @Column(nullable = false)
    private Long enrollmentId;

    @Column(unique = true, nullable = false)
    private String certificateNumber;

    @Column(nullable = false)
    private String status = "ISSUED";

    private Double finalGrade;
    private String letterGrade;

    @Column(nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    private LocalDateTime revokedAt;
    private String notes;
}
