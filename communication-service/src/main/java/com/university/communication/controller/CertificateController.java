package com.university.communication.controller;

import com.university.communication.dto.CertificateDto;
import com.university.communication.dto.IssueCertificateRequest;
import com.university.communication.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Certificate management endpoints")
public class CertificateController {
    private final CertificateService certificateService;

    @GetMapping
    @Operation(summary = "Get all certificates")
    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    public ResponseEntity<List<CertificateDto>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get certificate by ID")
    public ResponseEntity<CertificateDto> getCertificateById(
            @Parameter(description = "Certificate ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get certificates by student")
    public ResponseEntity<List<CertificateDto>> getCertificatesByStudent(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(certificateService.getCertificatesByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get certificates by course")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<List<CertificateDto>> getCertificatesByCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("courseId") Long courseId) {
        return ResponseEntity.ok(certificateService.getCertificatesByCourse(courseId));
    }

    @GetMapping("/number/{number}")
    @Operation(summary = "Verify certificate by number")
    public ResponseEntity<CertificateDto> getCertificateByNumber(
            @Parameter(description = "Certificate number (e.g. CERT-2026-001)", required = true, example = "CERT-2026-001")
            @PathVariable("number") String number) {
        return ResponseEntity.ok(certificateService.getCertificateByNumber(number));
    }

    @PostMapping("/{enrollmentId}/issue")
    @Operation(summary = "Issue certificate for an enrollment")
    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    public ResponseEntity<CertificateDto> issueCertificate(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable("enrollmentId") Long enrollmentId,
            @Valid @RequestBody IssueCertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.issueCertificate(enrollmentId, request));
    }

    @PatchMapping("/{id}/revoke")
    @Operation(summary = "Revoke a certificate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateDto> revokeCertificate(
            @Parameter(description = "Certificate ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(certificateService.revokeCertificate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete certificate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCertificate(
            @Parameter(description = "Certificate ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }
}
