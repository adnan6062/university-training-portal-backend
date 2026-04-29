package com.university.enrollment.service;

import com.university.enrollment.client.CatalogServiceClient;
import com.university.enrollment.client.IdentityServiceClient;
import com.university.enrollment.client.dto.SessionResponse;
import com.university.enrollment.client.dto.UserResponse;
import com.university.enrollment.dto.*;
import com.university.enrollment.entity.Enrollment;
import com.university.enrollment.entity.Invoice;
import com.university.enrollment.exception.AlreadyEnrolledException;
import com.university.enrollment.exception.ResourceNotFoundException;
import com.university.enrollment.exception.SessionFullException;
import com.university.enrollment.repository.EnrollmentRepository;
import com.university.enrollment.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final IdentityServiceClient identityServiceClient;

    @Transactional(readOnly = true)
    public List<EnrollmentDto> getAllEnrollments() {
        return enrollmentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDto> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDto> getEnrollmentsBySession(Long sessionId) {
        return enrollmentRepository.findBySessionId(sessionId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public EnrollmentDto getEnrollmentById(Long id) {
        return toDto(enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id)));
    }

    @Transactional(readOnly = true)
    public EnrollmentDto getEnrollmentByStudentAndSession(Long studentId, Long sessionId) {
        return toDto(enrollmentRepository.findBySessionIdAndStudentId(sessionId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No enrollment found for student " + studentId + " in session " + sessionId)));
    }

    @Transactional
    public EnrollmentDto enroll(Long sessionId, EnrollmentRequest request) {
        // 1. Validate student exists in identity-service
        UserResponse student;
        try {
            student = identityServiceClient.getUserById(request.getStudentId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Student not found with id: " + request.getStudentId());
        }

        // 2. Validate session exists and is enrollable in catalog-service
        SessionResponse session;
        try {
            session = catalogServiceClient.getSessionById(sessionId);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        // 3. Validate session is OPEN
        if (!"OPEN".equalsIgnoreCase(session.getStatus())) {
            throw new SessionFullException(
                    "Session " + sessionId + " is not open for enrollment (status: " + session.getStatus() + ")");
        }

        // 4. Validate session capacity
        if (session.getEnrolledCount() >= session.getCapacity()) {
            throw new SessionFullException(
                    "Session " + sessionId + " has reached its maximum capacity of " + session.getCapacity());
        }

        // 5. Validate student is not already enrolled
        if (enrollmentRepository.existsBySessionIdAndStudentId(sessionId, request.getStudentId())) {
            throw new AlreadyEnrolledException(
                    "Student " + request.getStudentId() + " is already enrolled in session " + sessionId);
        }

        // 6. Auto-fill student name from identity-service when not provided
        String studentName = request.getStudentName();
        if (studentName == null || studentName.isBlank()) {
            studentName = (student.getFirstName() != null ? student.getFirstName() : "")
                    + " " + (student.getLastName() != null ? student.getLastName() : "");
            studentName = studentName.trim();
            if (studentName.isEmpty()) studentName = student.getUsername();
        }

        // 7. Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setSessionId(sessionId);
        enrollment.setStudentId(request.getStudentId());
        enrollment.setStudentName(studentName);
        enrollment.setStatus("PENDING");
        enrollment = enrollmentRepository.save(enrollment);

        // 8. Create invoice
        Invoice invoice = new Invoice();
        invoice.setEnrollment(enrollment);
        invoice.setAmount(request.getAmount() != null ? request.getAmount() : BigDecimal.valueOf(500.00));
        invoice.setStatus("PENDING");
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setDescription(request.getDescription() != null
                ? request.getDescription()
                : "Enrollment fee for " + session.getCourseTitle() + " (Session " + sessionId + ")");
        invoice.setCurrency("USD");
        invoiceRepository.save(invoice);
        enrollment.setInvoice(invoice);

        // 9. Increment enrolled count in catalog-service
        try {
            catalogServiceClient.incrementEnrollment(sessionId);
        } catch (Exception ex) {
            log.warn("Failed to increment enrolled count for session {}: {}", sessionId, ex.getMessage());
        }

        return toDto(enrollment);
    }

    @Transactional
    public EnrollmentDto updateEnrollmentStatus(Long id, String status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollment.setStatus(status);
        if ("ACTIVE".equals(status)) enrollment.setActivatedAt(LocalDateTime.now());
        if ("COMPLETED".equals(status)) enrollment.setCompletedAt(LocalDateTime.now());
        return toDto(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public void cancelEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        String previousStatus = enrollment.getStatus();
        enrollment.setStatus("CANCELLED");
        enrollmentRepository.save(enrollment);

        // Decrement enrolled count in catalog-service only for non-cancelled enrollments
        if (!"CANCELLED".equals(previousStatus)) {
            try {
                catalogServiceClient.decrementEnrollment(enrollment.getSessionId());
            } catch (Exception ex) {
                log.warn("Failed to decrement enrolled count for session {}: {}",
                        enrollment.getSessionId(), ex.getMessage());
            }
        }
    }

    @Transactional
    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id))
            throw new ResourceNotFoundException("Enrollment not found with id: " + id);
        enrollmentRepository.deleteById(id);
    }

    private EnrollmentDto toDto(Enrollment e) {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setId(e.getId());
        dto.setSessionId(e.getSessionId());
        dto.setStudentId(e.getStudentId());
        dto.setStudentName(e.getStudentName());
        dto.setStatus(e.getStatus());
        dto.setEnrolledAt(e.getEnrolledAt());
        dto.setActivatedAt(e.getActivatedAt());
        dto.setCompletedAt(e.getCompletedAt());
        if (e.getInvoice() != null) dto.setInvoice(toInvoiceDto(e.getInvoice()));
        return dto;
    }

    private InvoiceDto toInvoiceDto(Invoice i) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(i.getId());
        dto.setEnrollmentId(i.getEnrollment().getId());
        dto.setAmount(i.getAmount());
        dto.setStatus(i.getStatus());
        dto.setDueDate(i.getDueDate());
        dto.setDescription(i.getDescription());
        dto.setCurrency(i.getCurrency());
        dto.setCreatedAt(i.getCreatedAt());
        return dto;
    }
}
