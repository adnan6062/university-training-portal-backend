package com.university.enrollment.controller;

import com.university.enrollment.dto.InvoiceDto;
import com.university.enrollment.dto.PaymentDto;
import com.university.enrollment.dto.PaymentRequest;
import com.university.enrollment.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invoices & Payments", description = "Invoice and payment endpoints")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping("/invoices")
    @Operation(summary = "Get all invoices")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTING')")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceDto> getInvoiceById(
            @Parameter(description = "Invoice ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/invoices/enrollment/{enrollmentId}")
    @Operation(summary = "Get invoice by enrollment")
    public ResponseEntity<InvoiceDto> getInvoiceByEnrollment(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable("enrollmentId") Long enrollmentId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByEnrollment(enrollmentId));
    }

    @PostMapping("/invoices/{id}/pay")
    @Operation(summary = "Pay an invoice (mock payment)")
    public ResponseEntity<InvoiceDto> payInvoice(
            @Parameter(description = "Invoice ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(invoiceService.payInvoice(id, request));
    }

    @PatchMapping("/invoices/{id}/status")
    @Operation(summary = "Update invoice status")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTING')")
    public ResponseEntity<InvoiceDto> updateInvoiceStatus(
            @Parameter(description = "Invoice ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, body.get("status")));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get all payments")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTING')")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(invoiceService.getAllPayments());
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentDto> getPaymentById(
            @Parameter(description = "Payment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(invoiceService.getPaymentById(id));
    }

    @GetMapping("/invoices/{id}/payments")
    @Operation(summary = "Get payments for an invoice")
    public ResponseEntity<List<PaymentDto>> getPaymentsByInvoice(
            @Parameter(description = "Invoice ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(invoiceService.getPaymentsByInvoice(id));
    }
}
