package com.university.catalog.controller;

import com.university.catalog.dto.SessionDto;
import com.university.catalog.service.SessionService;
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
@Tag(name = "Sessions", description = "Session management endpoints")
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/sessions")
    @Operation(summary = "Get all sessions")
    public ResponseEntity<List<SessionDto>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/sessions/{id}")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<SessionDto> getSessionById(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @GetMapping("/courses/{courseId}/sessions")
    @Operation(summary = "Get sessions by course")
    public ResponseEntity<List<SessionDto>> getSessionsByCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("courseId") Long courseId) {
        return ResponseEntity.ok(sessionService.getSessionsByCourse(courseId));
    }

    @PostMapping("/courses/{courseId}/sessions")
    @Operation(summary = "Create session for a course")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<SessionDto> createSession(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody SessionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession(courseId, dto));
    }

    @PutMapping("/sessions/{id}")
    @Operation(summary = "Update session")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<SessionDto> updateSession(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody SessionDto dto) {
        return ResponseEntity.ok(sessionService.updateSession(id, dto));
    }

    @DeleteMapping("/sessions/{id}")
    @Operation(summary = "Delete session")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sessions/{id}/increment-enrollment")
    @Operation(summary = "Increment enrolled count (called by enrollment service)")
    public ResponseEntity<SessionDto> incrementEnrollment(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(sessionService.incrementEnrolledCount(id));
    }

    @PostMapping("/sessions/{id}/decrement-enrollment")
    @Operation(summary = "Decrement enrolled count (called by enrollment service on cancellation)")
    public ResponseEntity<SessionDto> decrementEnrollment(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(sessionService.decrementEnrolledCount(id));
    }
}
