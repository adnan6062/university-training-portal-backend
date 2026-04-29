package com.university.catalog.controller;

import com.university.catalog.dto.CourseDto;
import com.university.catalog.service.CourseService;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all courses or search by query")
    public ResponseEntity<List<CourseDto>> getCourses(
            @Parameter(description = "Free-text search term", required = false, example = "Java")
            @RequestParam(name = "query", required = false) String query,
            @Parameter(description = "Filter by category", required = false, example = "Programming")
            @RequestParam(name = "category", required = false) String category,
            @Parameter(description = "Filter by level (BEGINNER, INTERMEDIATE, ADVANCED)", required = false, example = "BEGINNER")
            @RequestParam(name = "level", required = false) String level) {
        if (query != null) return ResponseEntity.ok(courseService.searchCourses(query));
        if (category != null) return ResponseEntity.ok(courseService.getCoursesByCategory(category));
        if (level != null) return ResponseEntity.ok(courseService.getCoursesByLevel(level));
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseDto> getCourseById(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get course by code")
    public ResponseEntity<CourseDto> getCourseByCode(
            @Parameter(description = "Course code", required = true, example = "CS101")
            @PathVariable("code") String code) {
        return ResponseEntity.ok(courseService.getCourseByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<CourseDto> updateCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody CourseDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
