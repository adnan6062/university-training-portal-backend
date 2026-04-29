package com.university.enrollment.controller;

import com.university.enrollment.dto.EnrollmentDto;
import com.university.enrollment.dto.EnrollmentRequest;
import com.university.enrollment.service.EnrollmentService;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Enrollment management endpoints")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @GetMapping("/enrollments")
    @Operation(summary = "Get all enrollments")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ACCOUNTING')")
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/enrollments/{id}")
    @Operation(summary = "Get enrollment by ID")
    public ResponseEntity<EnrollmentDto> getEnrollmentById(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/enrollments/student/{studentId}")
    @Operation(summary = "Get enrollments by student")
    public ResponseEntity<List<EnrollmentDto>> getEnrollmentsByStudent(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/enrollments/session/{sessionId}")
    @Operation(summary = "Get enrollments by session")
    public ResponseEntity<List<EnrollmentDto>> getEnrollmentsBySession(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsBySession(sessionId));
    }

    @GetMapping("/enrollments/student/{studentId}/session/{sessionId}")
    @Operation(summary = "Get enrollment by student and session (used for inter-service validation)")
    public ResponseEntity<EnrollmentDto> getEnrollmentByStudentAndSession(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId,
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentByStudentAndSession(studentId, sessionId));
    }

    @PostMapping("/sessions/{sessionId}/enroll")
    @Operation(summary = "Enroll a student in a session")
    public ResponseEntity<EnrollmentDto> enroll(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId,
            @Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(sessionId, request));
    }

    @PatchMapping("/enrollments/{id}/status")
    @Operation(summary = "Update enrollment status")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTING')")
    public ResponseEntity<EnrollmentDto> updateStatus(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
            return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, body.get("status")));
    }

    @PostMapping("/enrollments/{id}/cancel")
    @Operation(summary = "Cancel an enrollment")
    public ResponseEntity<Void> cancelEnrollment(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/enrollments/{id}")
    @Operation(summary = "Delete enrollment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEnrollment(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
