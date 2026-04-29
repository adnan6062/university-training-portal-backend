package com.university.assessment.service;

import com.university.assessment.client.CatalogServiceClient;
import com.university.assessment.client.EnrollmentServiceClient;
import com.university.assessment.client.dto.EnrollmentResponse;
import com.university.assessment.dto.*;
import com.university.assessment.entity.Exam;
import com.university.assessment.entity.ExamAttempt;
import com.university.assessment.exception.ResourceNotFoundException;
import com.university.assessment.exception.StudentNotEnrolledException;
import com.university.assessment.repository.ExamAttemptRepository;
import com.university.assessment.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;

    @Transactional(readOnly = true)
    public List<ExamDto> getAllExams() {
        return examRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ExamDto> getExamsBySession(Long sessionId) {
        return examRepository.findBySessionId(sessionId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ExamDto getExamById(Long id) {
        return toDto(examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id)));
    }

    @Transactional
    public ExamDto createExam(Long sessionId, ExamDto dto) {
        // Validate session exists in catalog-service
        try {
            catalogServiceClient.getSessionById(sessionId);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        Exam exam = new Exam();
        exam.setSessionId(sessionId);
        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setStartAt(dto.getStartAt());
        exam.setDuration(dto.getDuration());
        exam.setTotalMarks(dto.getTotalMarks() != null ? dto.getTotalMarks() : 100.0);
        exam.setPassingMarks(dto.getPassingMarks() != null ? dto.getPassingMarks() : 50.0);
        exam.setStatus("SCHEDULED");
        return toDto(examRepository.save(exam));
    }

    @Transactional
    public ExamDto updateExam(Long id, ExamDto dto) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setStartAt(dto.getStartAt());
        exam.setDuration(dto.getDuration());
        exam.setTotalMarks(dto.getTotalMarks());
        exam.setPassingMarks(dto.getPassingMarks());
        if (dto.getStatus() != null) exam.setStatus(dto.getStatus());
        return toDto(examRepository.save(exam));
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id))
            throw new ResourceNotFoundException("Exam not found with id: " + id);
        examRepository.deleteById(id);
    }

    @Transactional
    public ExamAttemptDto startAttempt(Long examId, ExamAttemptRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        // Validate student is enrolled in the session this exam belongs to
        validateStudentEnrolled(request.getStudentId(), exam.getSessionId());

        if (examAttemptRepository.existsByExamIdAndStudentId(examId, request.getStudentId())) {
            throw new RuntimeException("Student has already attempted this exam");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudentId(request.getStudentId());
        attempt.setStudentName(request.getStudentName());
        attempt.setAnswers(request.getAnswers());
        attempt.setStatus("IN_PROGRESS");
        attempt.setStartedAt(LocalDateTime.now());
        return toAttemptDto(examAttemptRepository.save(attempt));
    }

    @Transactional
    public ExamAttemptDto submitAttempt(Long examId, Long studentId, ExamAttemptRequest request) {
        ExamAttempt attempt = examAttemptRepository.findByExamIdAndStudentId(examId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active attempt found for student " + studentId + " in exam " + examId));
        attempt.setAnswers(request.getAnswers());
        attempt.setStatus("SUBMITTED");
        attempt.setSubmittedAt(LocalDateTime.now());
        return toAttemptDto(examAttemptRepository.save(attempt));
    }

    @Transactional
    public ExamAttemptDto gradeAttempt(Long attemptId, GradeSubmissionRequest request) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        attempt.setScore(request.getScore());
        attempt.setFeedback(request.getFeedback());
        attempt.setGradedBy(request.getGradedBy());
        attempt.setGradedAt(LocalDateTime.now());
        attempt.setStatus("GRADED");
        return toAttemptDto(examAttemptRepository.save(attempt));
    }

    // ExamAttempt.exam is FetchType.LAZY — keep session open with readOnly transaction
    @Transactional(readOnly = true)
    public List<ExamAttemptDto> getAttemptsByExam(Long examId) {
        return examAttemptRepository.findByExamId(examId).stream().map(this::toAttemptDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ExamAttemptDto> getAttemptsByStudent(Long studentId) {
        return examAttemptRepository.findByStudentId(studentId).stream().map(this::toAttemptDto).toList();
    }

    @Transactional(readOnly = true)
    public ExamAttemptDto getAttemptById(Long id) {
        return toAttemptDto(examAttemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + id)));
    }

    private void validateStudentEnrolled(Long studentId, Long sessionId) {
        try {
            EnrollmentResponse enrollment = enrollmentServiceClient
                    .getEnrollmentByStudentAndSession(studentId, sessionId);
            if ("CANCELLED".equalsIgnoreCase(enrollment.getStatus())) {
                throw new StudentNotEnrolledException(
                        "Student " + studentId + " has a cancelled enrollment in session " + sessionId
                        + ". Active enrollment is required to attempt exams.");
            }
        } catch (StudentNotEnrolledException ex) {
            throw ex;
        } catch (ResourceNotFoundException ex) {
            throw new StudentNotEnrolledException(
                    "Student " + studentId + " is not enrolled in session " + sessionId
                    + ". Enrollment is required to attempt exams.");
        } catch (Exception ex) {
            log.warn("Could not verify enrollment for student {} in session {}: {}",
                    studentId, sessionId, ex.getMessage());
        }
    }

    private ExamDto toDto(Exam e) {
        ExamDto dto = new ExamDto();
        dto.setId(e.getId());
        dto.setSessionId(e.getSessionId());
        dto.setTitle(e.getTitle());
        dto.setDescription(e.getDescription());
        dto.setStartAt(e.getStartAt());
        dto.setDuration(e.getDuration());
        dto.setTotalMarks(e.getTotalMarks());
        dto.setPassingMarks(e.getPassingMarks());
        dto.setStatus(e.getStatus());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    private ExamAttemptDto toAttemptDto(ExamAttempt a) {
        ExamAttemptDto dto = new ExamAttemptDto();
        dto.setId(a.getId());
        dto.setExamId(a.getExam().getId());
        dto.setStudentId(a.getStudentId());
        dto.setStudentName(a.getStudentName());
        dto.setScore(a.getScore());
        dto.setAnswers(a.getAnswers());
        dto.setFeedback(a.getFeedback());
        dto.setStatus(a.getStatus());
        dto.setStartedAt(a.getStartedAt());
        dto.setSubmittedAt(a.getSubmittedAt());
        dto.setGradedAt(a.getGradedAt());
        return dto;
    }
}
