package com.university.assessment.service;

import com.university.assessment.client.CatalogServiceClient;
import com.university.assessment.client.EnrollmentServiceClient;
import com.university.assessment.client.dto.EnrollmentResponse;
import com.university.assessment.dto.*;
import com.university.assessment.entity.Assignment;
import com.university.assessment.entity.Submission;
import com.university.assessment.exception.ResourceNotFoundException;
import com.university.assessment.exception.StudentNotEnrolledException;
import com.university.assessment.repository.AssignmentRepository;
import com.university.assessment.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;

    @Transactional(readOnly = true)
    public List<AssignmentDto> getAllAssignments() {
        return assignmentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> getAssignmentsBySession(Long sessionId) {
        return assignmentRepository.findBySessionId(sessionId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AssignmentDto getAssignmentById(Long id) {
        return toDto(assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id)));
    }

    @Transactional
    public AssignmentDto createAssignment(Long sessionId, AssignmentDto dto) {
        // Validate session exists in catalog-service
        try {
            catalogServiceClient.getSessionById(sessionId);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        Assignment assignment = new Assignment();
        assignment.setSessionId(sessionId);
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setMaxScore(dto.getMaxScore() != null ? dto.getMaxScore() : 100.0);
        assignment.setStatus("ACTIVE");
        return toDto(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentDto updateAssignment(Long id, AssignmentDto dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setMaxScore(dto.getMaxScore());
        if (dto.getStatus() != null) assignment.setStatus(dto.getStatus());
        return toDto(assignmentRepository.save(assignment));
    }

    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id))
            throw new ResourceNotFoundException("Assignment not found with id: " + id);
        assignmentRepository.deleteById(id);
    }

    @Transactional
    public SubmissionDto submitAssignment(Long assignmentId, SubmitAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        // Validate student is enrolled in the session this assignment belongs to
        validateStudentEnrolled(request.getStudentId(), assignment.getSessionId());

        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, request.getStudentId())) {
            throw new RuntimeException("Student has already submitted this assignment");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudentId(request.getStudentId());
        submission.setStudentName(request.getStudentName());
        submission.setContent(request.getContent());
        submission.setFileUrl(request.getFileUrl());
        submission.setStatus("SUBMITTED");
        return toSubmissionDto(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionDto gradeSubmission(Long submissionId, GradeSubmissionRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setGradedBy(request.getGradedBy());
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus("GRADED");
        return toSubmissionDto(submissionRepository.save(submission));
    }

    // Submission.assignment is FetchType.LAZY — keep session open with readOnly transaction
    @Transactional(readOnly = true)
    public List<SubmissionDto> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream().map(this::toSubmissionDto).toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionDto> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId).stream().map(this::toSubmissionDto).toList();
    }

    @Transactional(readOnly = true)
    public SubmissionDto getSubmissionById(Long id) {
        return toSubmissionDto(submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id)));
    }

    @Transactional
    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id))
            throw new ResourceNotFoundException("Submission not found with id: " + id);
        submissionRepository.deleteById(id);
    }

    private void validateStudentEnrolled(Long studentId, Long sessionId) {
        try {
            EnrollmentResponse enrollment = enrollmentServiceClient
                    .getEnrollmentByStudentAndSession(studentId, sessionId);
            if ("CANCELLED".equalsIgnoreCase(enrollment.getStatus())) {
                throw new StudentNotEnrolledException(
                        "Student " + studentId + " has a cancelled enrollment in session " + sessionId
                        + ". Active enrollment is required to submit assignments.");
            }
        } catch (StudentNotEnrolledException ex) {
            throw ex;
        } catch (ResourceNotFoundException ex) {
            throw new StudentNotEnrolledException(
                    "Student " + studentId + " is not enrolled in session " + sessionId
                    + ". Enrollment is required to submit assignments.");
        } catch (Exception ex) {
            log.warn("Could not verify enrollment for student {} in session {}: {}",
                    studentId, sessionId, ex.getMessage());
        }
    }

    private AssignmentDto toDto(Assignment a) {
        AssignmentDto dto = new AssignmentDto();
        dto.setId(a.getId());
        dto.setSessionId(a.getSessionId());
        dto.setTitle(a.getTitle());
        dto.setDescription(a.getDescription());
        dto.setDueDate(a.getDueDate());
        dto.setMaxScore(a.getMaxScore());
        dto.setStatus(a.getStatus());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }

    private SubmissionDto toSubmissionDto(Submission s) {
        SubmissionDto dto = new SubmissionDto();
        dto.setId(s.getId());
        dto.setAssignmentId(s.getAssignment().getId());
        dto.setStudentId(s.getStudentId());
        dto.setStudentName(s.getStudentName());
        dto.setContent(s.getContent());
        dto.setFileUrl(s.getFileUrl());
        dto.setScore(s.getScore());
        dto.setFeedback(s.getFeedback());
        dto.setStatus(s.getStatus());
        dto.setSubmittedAt(s.getSubmittedAt());
        dto.setGradedAt(s.getGradedAt());
        return dto;
    }
}
