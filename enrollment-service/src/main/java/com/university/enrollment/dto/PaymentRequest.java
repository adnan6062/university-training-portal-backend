package com.university.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull private BigDecimal amount;
    private String paymentMethod = "MOCK";
    private String notes;
}
