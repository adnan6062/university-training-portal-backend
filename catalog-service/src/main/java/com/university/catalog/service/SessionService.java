package com.university.catalog.service;

import com.university.catalog.dto.SessionDto;
import com.university.catalog.entity.Course;
import com.university.catalog.entity.Session;
import com.university.catalog.repository.CourseRepository;
import com.university.catalog.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<SessionDto> getSessionsByCourse(Long courseId) {
        return sessionRepository.findByCourseId(courseId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SessionDto getSessionById(Long id) {
        return toDto(sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id)));
    }

    @Transactional
    public SessionDto createSession(Long courseId, SessionDto dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        Session session = new Session();
        session.setCourse(course);
        session.setStartDate(dto.getStartDate());
        session.setEndDate(dto.getEndDate());
        session.setCapacity(dto.getCapacity());
        session.setMode(dto.getMode() != null ? dto.getMode() : "online");
        session.setInstructor(dto.getInstructor());
        session.setLocation(dto.getLocation());
        session.setStatus("OPEN");
        return toDto(sessionRepository.save(session));
    }

    @Transactional
    public SessionDto updateSession(Long id, SessionDto dto) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        session.setStartDate(dto.getStartDate());
        session.setEndDate(dto.getEndDate());
        session.setCapacity(dto.getCapacity());
        session.setMode(dto.getMode());
        session.setInstructor(dto.getInstructor());
        session.setLocation(dto.getLocation());
        if (dto.getStatus() != null) session.setStatus(dto.getStatus());
        return toDto(sessionRepository.save(session));
    }

    @Transactional
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) throw new RuntimeException("Session not found with id: " + id);
        sessionRepository.deleteById(id);
    }

    @Transactional
    public SessionDto incrementEnrolledCount(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        if (session.getEnrolledCount() >= session.getCapacity()) {
            throw new RuntimeException("Session is at full capacity");
        }
        session.setEnrolledCount(session.getEnrolledCount() + 1);
        if (session.getEnrolledCount().equals(session.getCapacity())) session.setStatus("FULL");
        return toDto(sessionRepository.save(session));
    }

    @Transactional
    public SessionDto decrementEnrolledCount(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        if (session.getEnrolledCount() > 0) {
            session.setEnrolledCount(session.getEnrolledCount() - 1);
        }
        if ("FULL".equals(session.getStatus())) session.setStatus("OPEN");
        return toDto(sessionRepository.save(session));
    }

    private SessionDto toDto(Session s) {
        SessionDto dto = new SessionDto();
        dto.setId(s.getId());
        dto.setCourseId(s.getCourse().getId());
        dto.setCourseCode(s.getCourse().getCode());
        dto.setCourseTitle(s.getCourse().getTitle());
        dto.setStartDate(s.getStartDate());
        dto.setEndDate(s.getEndDate());
        dto.setCapacity(s.getCapacity());
        dto.setEnrolledCount(s.getEnrolledCount());
        dto.setMode(s.getMode());
        dto.setInstructor(s.getInstructor());
        dto.setLocation(s.getLocation());
        dto.setStatus(s.getStatus());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
