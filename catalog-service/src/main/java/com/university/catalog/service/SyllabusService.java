package com.university.catalog.service;

import com.university.catalog.dto.SyllabusDto;
import com.university.catalog.entity.Course;
import com.university.catalog.entity.Syllabus;
import com.university.catalog.repository.CourseRepository;
import com.university.catalog.repository.SyllabusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SyllabusService {
    private final SyllabusRepository syllabusRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<SyllabusDto> getAllSyllabi() {
        return syllabusRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SyllabusDto getSyllabusByCourse(Long courseId) {
        return toDto(syllabusRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Syllabus not found for course: " + courseId)));
    }

    @Transactional(readOnly = true)
    public SyllabusDto getSyllabusById(Long id) {
        return toDto(syllabusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Syllabus not found with id: " + id)));
    }

    @Transactional
    public SyllabusDto createOrUpdateSyllabus(Long courseId, SyllabusDto dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        Syllabus syllabus = syllabusRepository.findByCourseId(courseId).orElse(new Syllabus());
        syllabus.setCourse(course);
        syllabus.setContent(dto.getContent());
        syllabus.setObjectives(dto.getObjectives());
        syllabus.setPrerequisites(dto.getPrerequisites());
        syllabus.setTextbooks(dto.getTextbooks());
        return toDto(syllabusRepository.save(syllabus));
    }

    @Transactional
    public void deleteSyllabus(Long id) {
        if (!syllabusRepository.existsById(id)) throw new RuntimeException("Syllabus not found with id: " + id);
        syllabusRepository.deleteById(id);
    }

    private SyllabusDto toDto(Syllabus s) {
        SyllabusDto dto = new SyllabusDto();
        dto.setId(s.getId());
        dto.setCourseId(s.getCourse().getId());
        dto.setContent(s.getContent());
        dto.setObjectives(s.getObjectives());
        dto.setPrerequisites(s.getPrerequisites());
        dto.setTextbooks(s.getTextbooks());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
