package com.university.assessment.service;

import com.university.assessment.dto.GradeDto;
import com.university.assessment.entity.Grade;
import com.university.assessment.exception.ResourceNotFoundException;
import com.university.assessment.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public List<GradeDto> getAllGrades() {
        return gradeRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesBySession(Long sessionId) {
        return gradeRepository.findBySessionId(sessionId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public GradeDto getGradeById(Long id) {
        return toDto(gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id)));
    }

    @Transactional(readOnly = true)
    public GradeDto getGradeByStudentAndSession(Long studentId, Long sessionId) {
        return toDto(gradeRepository.findByStudentIdAndSessionId(studentId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grade not found for student " + studentId + " in session " + sessionId)));
    }

    @Transactional
    public GradeDto createOrUpdateGrade(GradeDto dto) {
        Grade grade = gradeRepository.findByStudentIdAndSessionId(dto.getStudentId(), dto.getSessionId())
                .orElse(new Grade());
        grade.setStudentId(dto.getStudentId());
        grade.setSessionId(dto.getSessionId());
        grade.setEnrollmentId(dto.getEnrollmentId());
        grade.setAssignmentScore(dto.getAssignmentScore());
        grade.setExamScore(dto.getExamScore());

        if (dto.getAssignmentScore() != null && dto.getExamScore() != null) {
            double finalGrade = (dto.getAssignmentScore() * 0.4) + (dto.getExamScore() * 0.6);
            grade.setFinalGrade(finalGrade);
            grade.setLetterGrade(calculateLetterGrade(finalGrade));
            grade.setStatus("FINALIZED");
        } else {
            if (dto.getFinalGrade() != null) {
                grade.setFinalGrade(dto.getFinalGrade());
                grade.setLetterGrade(calculateLetterGrade(dto.getFinalGrade()));
            }
            grade.setStatus("PENDING");
        }
        return toDto(gradeRepository.save(grade));
    }

    @Transactional
    public void deleteGrade(Long id) {
        if (!gradeRepository.existsById(id)) throw new ResourceNotFoundException("Grade not found with id: " + id);
        gradeRepository.deleteById(id);
    }

    private String calculateLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private GradeDto toDto(Grade g) {
        GradeDto dto = new GradeDto();
        dto.setId(g.getId());
        dto.setStudentId(g.getStudentId());
        dto.setSessionId(g.getSessionId());
        dto.setEnrollmentId(g.getEnrollmentId());
        dto.setAssignmentScore(g.getAssignmentScore());
        dto.setExamScore(g.getExamScore());
        dto.setFinalGrade(g.getFinalGrade());
        dto.setLetterGrade(g.getLetterGrade());
        dto.setStatus(g.getStatus());
        dto.setCreatedAt(g.getCreatedAt());
        return dto;
    }
}
