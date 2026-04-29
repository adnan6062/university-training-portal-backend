package com.university.catalog.controller;

import com.university.catalog.dto.SyllabusDto;
import com.university.catalog.service.SyllabusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Syllabi", description = "Syllabus management endpoints")
public class SyllabusController {
    private final SyllabusService syllabusService;

    @GetMapping("/{courseId}/syllabus")
    @Operation(summary = "Get syllabus for a course")
    public ResponseEntity<SyllabusDto> getSyllabus(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("courseId") Long courseId) {
        return ResponseEntity.ok(syllabusService.getSyllabusByCourse(courseId));
    }

    @PutMapping("/{courseId}/syllabus")
    @Operation(summary = "Create or update syllabus for a course")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<SyllabusDto> createOrUpdateSyllabus(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("courseId") Long courseId,
            @RequestBody SyllabusDto dto) {
        return ResponseEntity.ok(syllabusService.createOrUpdateSyllabus(courseId, dto));
    }

    @DeleteMapping("/syllabi/{id}")
    @Operation(summary = "Delete syllabus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSyllabus(
            @Parameter(description = "Syllabus ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        syllabusService.deleteSyllabus(id);
        return ResponseEntity.noContent().build();
    }
}
