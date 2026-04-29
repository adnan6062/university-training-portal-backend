package com.university.enrollment.service;

import com.university.enrollment.dto.InvoiceDto;
import com.university.enrollment.dto.PaymentDto;
import com.university.enrollment.dto.PaymentRequest;
import com.university.enrollment.entity.Enrollment;
import com.university.enrollment.entity.Invoice;
import com.university.enrollment.entity.Payment;
import com.university.enrollment.repository.EnrollmentRepository;
import com.university.enrollment.repository.InvoiceRepository;
import com.university.enrollment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional          // class-level default: every public method runs in a transaction
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    // ── Read methods ──────────────────────────────────────────────────────────

    /**
     * Uses findAllWithDetails (LEFT JOIN FETCH enrollment + payments) so that
     * toDto() never tries to lazily load associations outside an open session.
     */
    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAllWithDetails().stream().map(this::toDto).toList();
    }

    /**
     * Uses findByIdWithDetails so that both Invoice.enrollment (OneToOne LAZY)
     * and Invoice.payments (OneToMany LAZY) are eagerly loaded in one query.
     *
     * Root cause of previous failure: plain findById() left those two associations
     * as Hibernate proxies; toDto() then tried to access them after the session
     * was closed → LazyInitializationException "no Session".
     */
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) {
        return toDto(invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id)));
    }

    /**
     * findByEnrollmentIdWithDetails already uses LEFT JOIN FETCH, so associations
     * are initialized.  The @Param("enrollmentId") in the repository ensures the
     * named parameter is bound correctly even without the -parameters compiler flag.
     */
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceByEnrollment(Long enrollmentId) {
        return toDto(invoiceRepository.findByEnrollmentIdWithDetails(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for enrollment: " + enrollmentId)));
    }

    // ── Write methods ─────────────────────────────────────────────────────────

    /**
     * Process a payment for the invoice.
     * Re-fetches via findByIdWithDetails after all saves so the returned DTO
     * includes the newly persisted Payment row in its payments list.
     */
    @Transactional
    public InvoiceDto payInvoice(Long invoiceId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Invoice is already paid");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount() != null ? request.getAmount() : invoice.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setNotes(request.getNotes());
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        Enrollment enrollment = invoice.getEnrollment();
        enrollment.setStatus("ACTIVE");
        enrollment.setActivatedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        // Re-fetch so the response DTO contains the newly saved payment
        return toDto(invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId)));
    }

    /**
     * Fetch with JOIN FETCH so payments are already in-memory, update status,
     * save, then map the in-memory (fully-loaded) entity – no lazy init needed.
     *
     * Root cause of previous failure: plain findById() + toDto(save(invoice))
     * → the saved entity had an uninitialized payments proxy → LazyInitializationException.
     */
    @Transactional
    public InvoiceDto updateInvoiceStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        invoice.setStatus(status);
        invoiceRepository.save(invoice);
        return toDto(invoice);   // payments already initialised by findByIdWithDetails
    }

    // ── Payment read methods ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream().map(this::toPaymentDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toPaymentDto).toList();
    }

    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long id) {
        return toPaymentDto(paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id)));
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private InvoiceDto toDto(Invoice i) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(i.getId());
        dto.setEnrollmentId(i.getEnrollment().getId());
        dto.setAmount(i.getAmount());
        dto.setStatus(i.getStatus());
        dto.setDueDate(i.getDueDate());
        dto.setDescription(i.getDescription());
        dto.setCurrency(i.getCurrency());
        dto.setCreatedAt(i.getCreatedAt());
        dto.setPayments(i.getPayments().stream().map(this::toPaymentDto).collect(Collectors.toList()));
        return dto;
    }

    private PaymentDto toPaymentDto(Payment p) {
        PaymentDto dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setInvoiceId(p.getInvoice().getId());
        dto.setAmount(p.getAmount());
        dto.setProviderRef(p.getProviderRef());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setStatus(p.getStatus());
        dto.setPaidAt(p.getPaidAt());
        dto.setTransactionId(p.getTransactionId());
        dto.setNotes(p.getNotes());
        return dto;
    }
}
