package com.university.assessment.controller;

import com.university.assessment.dto.GradeDto;
import com.university.assessment.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Grade management endpoints")
public class GradeController {
    private final GradeService gradeService;

    @GetMapping
    @Operation(summary = "Get all grades")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<List<GradeDto>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grade by ID")
    public ResponseEntity<GradeDto> getGradeById(
            @Parameter(description = "Grade ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get grades by student")
    public ResponseEntity<List<GradeDto>> getGradesByStudent(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get grades by session")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<List<GradeDto>> getGradesBySession(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(gradeService.getGradesBySession(sessionId));
    }

    @GetMapping("/student/{studentId}/session/{sessionId}")
    @Operation(summary = "Get grade for a student in a specific session (used for inter-service validation)")
    public ResponseEntity<GradeDto> getGradeByStudentAndSession(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId,
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(gradeService.getGradeByStudentAndSession(studentId, sessionId));
    }

    @PostMapping
    @Operation(summary = "Create or update grade")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<GradeDto> createOrUpdateGrade(@RequestBody GradeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeService.createOrUpdateGrade(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete grade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGrade(
            @Parameter(description = "Grade ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
