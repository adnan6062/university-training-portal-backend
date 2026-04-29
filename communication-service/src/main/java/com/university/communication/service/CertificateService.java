package com.university.communication.service;

import com.university.communication.client.AssessmentServiceClient;
import com.university.communication.client.CatalogServiceClient;
import com.university.communication.client.EnrollmentServiceClient;
import com.university.communication.client.IdentityServiceClient;
import com.university.communication.client.dto.EnrollmentResponse;
import com.university.communication.client.dto.GradeResponse;
import com.university.communication.client.dto.SessionResponse;
import com.university.communication.client.dto.UserResponse;
import com.university.communication.dto.CertificateDto;
import com.university.communication.dto.IssueCertificateRequest;
import com.university.communication.entity.Certificate;
import com.university.communication.exception.EnrollmentNotCompletedException;
import com.university.communication.exception.ResourceNotFoundException;
import com.university.communication.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final EnrollmentServiceClient enrollmentServiceClient;
    private final IdentityServiceClient identityServiceClient;
    private final AssessmentServiceClient assessmentServiceClient;
    private final CatalogServiceClient catalogServiceClient;

    public List<CertificateDto> getAllCertificates() {
        return certificateRepository.findAll().stream().map(this::toDto).toList();
    }

    public CertificateDto getCertificateById(Long id) {
        return toDto(certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + id)));
    }

    public List<CertificateDto> getCertificatesByStudent(Long studentId) {
        return certificateRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
    }

    public List<CertificateDto> getCertificatesByCourse(Long courseId) {
        return certificateRepository.findByCourseId(courseId).stream().map(this::toDto).toList();
    }

    public CertificateDto getCertificateByEnrollment(Long enrollmentId) {
        return toDto(certificateRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found for enrollment: " + enrollmentId)));
    }

    public CertificateDto getCertificateByNumber(String number) {
        return toDto(certificateRepository.findByCertificateNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found with number: " + number)));
    }

    @Transactional
    public CertificateDto issueCertificate(Long enrollmentId, IssueCertificateRequest request) {
        // 1. Validate certificate not already issued
        if (certificateRepository.existsByEnrollmentId(enrollmentId)) {
            throw new RuntimeException("Certificate already issued for enrollment: " + enrollmentId);
        }

        // 2. Validate enrollment exists and is COMPLETED via enrollment-service
        EnrollmentResponse enrollment;
        try {
            enrollment = enrollmentServiceClient.getEnrollmentById(enrollmentId);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId);
        }

        if (!"COMPLETED".equalsIgnoreCase(enrollment.getStatus())) {
            throw new EnrollmentNotCompletedException(
                    "Cannot issue certificate: enrollment " + enrollmentId
                    + " has status '" + enrollment.getStatus()
                    + "'. Only COMPLETED enrollments are eligible.");
        }

        // 3. Auto-fill student info from identity-service if not provided
        Long studentId = request.getStudentId() != null ? request.getStudentId() : enrollment.getStudentId();
        String studentName = request.getStudentName();
        String studentEmail = request.getStudentEmail();

        if (studentName == null || studentName.isBlank() || studentEmail == null || studentEmail.isBlank()) {
            try {
                UserResponse user = identityServiceClient.getUserById(studentId);
                if (studentName == null || studentName.isBlank()) {
                    String fullName = ((user.getFirstName() != null ? user.getFirstName() : "")
                            + " " + (user.getLastName() != null ? user.getLastName() : "")).trim();
                    studentName = fullName.isEmpty() ? user.getUsername() : fullName;
                }
                if (studentEmail == null || studentEmail.isBlank()) {
                    studentEmail = user.getEmail();
                }
            } catch (Exception ex) {
                log.warn("Could not fetch student info from identity-service for student {}: {}",
                        studentId, ex.getMessage());
                if (studentName == null) studentName = enrollment.getStudentName();
            }
        }

        // 4. Auto-fill course info from catalog-service if courseTitle not provided
        Long courseId = request.getCourseId();
        String courseTitle = request.getCourseTitle();

        if (courseTitle == null || courseTitle.isBlank()) {
            try {
                SessionResponse session = catalogServiceClient.getSessionById(enrollment.getSessionId());
                if (courseId == null) courseId = session.getCourseId();
                courseTitle = session.getCourseTitle();
            } catch (Exception ex) {
                log.warn("Could not fetch session/course info from catalog-service for session {}: {}",
                        enrollment.getSessionId(), ex.getMessage());
            }
        }

        // 5. Auto-fill grade info from assessment-service if not provided
        Double finalGrade = request.getFinalGrade();
        String letterGrade = request.getLetterGrade();

        if (finalGrade == null || letterGrade == null) {
            try {
                GradeResponse grade = assessmentServiceClient
                        .getGradeByStudentAndSession(studentId, enrollment.getSessionId());
                if (finalGrade == null) finalGrade = grade.getFinalGrade();
                if (letterGrade == null) letterGrade = grade.getLetterGrade();
            } catch (ResourceNotFoundException ex) {
                log.warn("No grade found for student {} in session {} — issuing certificate without grade data",
                        studentId, enrollment.getSessionId());
            } catch (Exception ex) {
                log.warn("Could not fetch grade from assessment-service: {}", ex.getMessage());
            }
        }

        // 6. Create and persist the certificate
        Certificate certificate = new Certificate();
        certificate.setStudentId(studentId);
        certificate.setStudentName(studentName);
        certificate.setStudentEmail(studentEmail);
        certificate.setCourseId(courseId);
        certificate.setCourseTitle(courseTitle);
        certificate.setEnrollmentId(enrollmentId);
        certificate.setCertificateNumber("CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        certificate.setFinalGrade(finalGrade);
        certificate.setLetterGrade(letterGrade);
        certificate.setNotes(request.getNotes());
        certificate.setStatus("ISSUED");
        certificate.setIssuedAt(LocalDateTime.now());
        return toDto(certificateRepository.save(certificate));
    }

    @Transactional
    public CertificateDto revokeCertificate(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + id));
        certificate.setStatus("REVOKED");
        certificate.setRevokedAt(LocalDateTime.now());
        return toDto(certificateRepository.save(certificate));
    }

    @Transactional
    public void deleteCertificate(Long id) {
        if (!certificateRepository.existsById(id))
            throw new ResourceNotFoundException("Certificate not found with id: " + id);
        certificateRepository.deleteById(id);
    }

    private CertificateDto toDto(Certificate c) {
        CertificateDto dto = new CertificateDto();
        dto.setId(c.getId());
        dto.setStudentId(c.getStudentId());
        dto.setStudentName(c.getStudentName());
        dto.setStudentEmail(c.getStudentEmail());
        dto.setCourseId(c.getCourseId());
        dto.setCourseTitle(c.getCourseTitle());
        dto.setEnrollmentId(c.getEnrollmentId());
        dto.setCertificateNumber(c.getCertificateNumber());
        dto.setStatus(c.getStatus());
        dto.setFinalGrade(c.getFinalGrade());
        dto.setLetterGrade(c.getLetterGrade());
        dto.setIssuedAt(c.getIssuedAt());
        dto.setNotes(c.getNotes());
        return dto;
    }
}
