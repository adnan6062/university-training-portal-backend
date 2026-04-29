package com.university.enrollment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDto {
    private Long id;
    private Long enrollmentId;
    private BigDecimal amount;
    private String status;
    private LocalDate dueDate;
    private String description;
    private String currency;
    private List<PaymentDto> payments;
    private LocalDateTime createdAt;
}
