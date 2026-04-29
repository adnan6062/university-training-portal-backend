package com.university.assessment.controller;

import com.university.assessment.dto.*;
import com.university.assessment.service.AssignmentService;
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
@RequiredArgsConstructor
@Tag(name = "Assignments", description = "Assignment management endpoints")
public class AssignmentController {
    private final AssignmentService assignmentService;
    
    @GetMapping("/assignments")
    @Operation(summary = "Get all assignments")
    public ResponseEntity<List<AssignmentDto>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/assignments/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<AssignmentDto> getAssignmentById(
            @Parameter(description = "Assignment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @GetMapping("/sessions/{sessionId}/assignments")
    @Operation(summary = "Get assignments by session")
    public ResponseEntity<List<AssignmentDto>> getAssignmentsBySession(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsBySession(sessionId));
    }

    @PostMapping("/sessions/{sessionId}/assignments")
    @Operation(summary = "Create assignment for a session")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<AssignmentDto> createAssignment(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId,
            @Valid @RequestBody AssignmentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(sessionId, dto));
    }

    @PutMapping("/assignments/{id}")
    @Operation(summary = "Update assignment")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<AssignmentDto> updateAssignment(
            @Parameter(description = "Assignment ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody AssignmentDto dto) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, dto));
    }

    @DeleteMapping("/assignments/{id}")
    @Operation(summary = "Delete assignment")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<Void> deleteAssignment(
            @Parameter(description = "Assignment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assignments/{id}/submit")
    @Operation(summary = "Submit an assignment")
    public ResponseEntity<SubmissionDto> submitAssignment(
            @Parameter(description = "Assignment ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody SubmitAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.submitAssignment(id, request));
    }

    @GetMapping("/assignments/{id}/submissions")
    @Operation(summary = "Get all submissions for an assignment")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<List<SubmissionDto>> getSubmissions(
            @Parameter(description = "Assignment ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByAssignment(id));
    }

    @GetMapping("/submissions/{id}")
    @Operation(summary = "Get submission by ID")
    public ResponseEntity<SubmissionDto> getSubmissionById(
            @Parameter(description = "Submission ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(assignmentService.getSubmissionById(id));
    }

    @GetMapping("/submissions/student/{studentId}")
    @Operation(summary = "Get submissions by student")
    public ResponseEntity<List<SubmissionDto>> getSubmissionsByStudent(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByStudent(studentId));
    }

    @PatchMapping("/submissions/{id}/grade")
    @Operation(summary = "Grade a submission")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<SubmissionDto> gradeSubmission(
            @Parameter(description = "Submission ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody GradeSubmissionRequest request) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(id, request));
    }

    @DeleteMapping("/submissions/{id}")
    @Operation(summary = "Delete submission")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubmission(
            @Parameter(description = "Submission ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        assignmentService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
