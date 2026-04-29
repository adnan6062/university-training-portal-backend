package com.university.communication.client.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SessionResponse {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer capacity;
    private Integer enrolledCount;
    private String mode;
    private String instructor;
    private String location;
    private String status;
}
