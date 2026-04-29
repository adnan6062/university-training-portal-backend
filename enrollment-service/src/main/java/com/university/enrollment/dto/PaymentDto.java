package com.university.enrollment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;
    private Long invoiceId;
    private BigDecimal amount;
    private String providerRef;
    private String paymentMethod;
    private String status;
    private LocalDateTime paidAt;
    private String transactionId;
    private String notes;
}
