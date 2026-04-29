package com.university.catalog.service;

import com.university.catalog.dto.CourseDto;
import com.university.catalog.entity.Course;
import com.university.catalog.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<CourseDto> searchCourses(String query) {
        return courseRepository.searchCourses(query).stream().map(this::toDto).toList();
    }

    public List<CourseDto> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category).stream().map(this::toDto).toList();
    }

    public List<CourseDto> getCoursesByLevel(String level) {
        return courseRepository.findByLevel(level).stream().map(this::toDto).toList();
    }

    public CourseDto getCourseById(Long id) {
        return toDto(courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id)));
    }

    public CourseDto getCourseByCode(String code) {
        return toDto(courseRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + code)));
    }

    @Transactional
    public CourseDto createCourse(CourseDto dto) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Course code already exists: " + dto.getCode());
        }
        Course course = new Course();
        course.setCode(dto.getCode());
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCategory(dto.getCategory());
        course.setLevel(dto.getLevel());
        course.setCreditHours(dto.getCreditHours());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        return toDto(courseRepository.save(course));
    }

    @Transactional
    public CourseDto updateCourse(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCategory(dto.getCategory());
        course.setLevel(dto.getLevel());
        course.setCreditHours(dto.getCreditHours());
        if (dto.getStatus() != null) course.setStatus(dto.getStatus());
        return toDto(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) throw new RuntimeException("Course not found with id: " + id);
        courseRepository.deleteById(id);
    }

    private CourseDto toDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setLevel(course.getLevel());
        dto.setCreditHours(course.getCreditHours());
        dto.setStatus(course.getStatus());
        dto.setCreatedAt(course.getCreatedAt());
        return dto;
    }
}
