package com.university.assessment.controller;

import com.university.assessment.dto.*;
import com.university.assessment.service.ExamService;
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
@Tag(name = "Exams", description = "Exam management endpoints")
public class ExamController {
    private final ExamService examService;

    @GetMapping("/exams")
    @Operation(summary = "Get all exams")
    public ResponseEntity<List<ExamDto>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @GetMapping("/exams/{id}")
    @Operation(summary = "Get exam by ID")
    public ResponseEntity<ExamDto> getExamById(
            @Parameter(description = "Exam ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @GetMapping("/sessions/{sessionId}/exams")
    @Operation(summary = "Get exams by session")
    public ResponseEntity<List<ExamDto>> getExamsBySession(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(examService.getExamsBySession(sessionId));
    }

    @PostMapping("/sessions/{sessionId}/exams")
    @Operation(summary = "Create exam for a session")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<ExamDto> createExam(
            @Parameter(description = "Session ID", required = true, example = "1")
            @PathVariable("sessionId") Long sessionId,
            @Valid @RequestBody ExamDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(sessionId, dto));
    }

    @PutMapping("/exams/{id}")
    @Operation(summary = "Update exam")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<ExamDto> updateExam(
            @Parameter(description = "Exam ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody ExamDto dto) {
        return ResponseEntity.ok(examService.updateExam(id, dto));
    }

    @DeleteMapping("/exams/{id}")
    @Operation(summary = "Delete exam")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<Void> deleteExam(
            @Parameter(description = "Exam ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exams/{id}/attempt")
    @Operation(summary = "Start an exam attempt")
    public ResponseEntity<ExamAttemptDto> startAttempt(
            @Parameter(description = "Exam ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody ExamAttemptRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.startAttempt(id, request));
    }

    @PostMapping("/exams/{examId}/submit/{studentId}")
    @Operation(summary = "Submit exam answers")
    public ResponseEntity<ExamAttemptDto> submitAttempt(
            @Parameter(description = "Exam ID", required = true, example = "1")
            @PathVariable("examId") Long examId,
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId,
            @RequestBody ExamAttemptRequest request) {
        return ResponseEntity.ok(examService.submitAttempt(examId, studentId, request));
    }

    @GetMapping("/exams/{id}/attempts")
    @Operation(summary = "Get all attempts for an exam")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<List<ExamAttemptDto>> getAttemptsByExam(
            @Parameter(description = "Exam ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(examService.getAttemptsByExam(id));
    }

    @GetMapping("/attempts/{id}")
    @Operation(summary = "Get attempt by ID")
    public ResponseEntity<ExamAttemptDto> getAttemptById(
            @Parameter(description = "Attempt ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(examService.getAttemptById(id));
    }

    @GetMapping("/attempts/student/{studentId}")
    @Operation(summary = "Get attempts by student")
    public ResponseEntity<List<ExamAttemptDto>> getAttemptsByStudent(
            @Parameter(description = "Student (user) ID", required = true, example = "1")
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(examService.getAttemptsByStudent(studentId));
    }

    @PatchMapping("/attempts/{id}/grade")
    @Operation(summary = "Grade an exam attempt")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<ExamAttemptDto> gradeAttempt(
            @Parameter(description = "Attempt ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody GradeSubmissionRequest request) {
        return ResponseEntity.ok(examService.gradeAttempt(id, request));
    }
}
