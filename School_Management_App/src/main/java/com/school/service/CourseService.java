package com.school.service;

import com.school.dto.CourseDTO;
import java.util.List;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
    CourseDTO getCourseById(Long id);
    CourseDTO getCourseByCode(String code);
    List<CourseDTO> getAllCourses();
    List<CourseDTO> getCoursesBySemester(Integer semester);
    List<CourseDTO> getCoursesByTeacherId(Long teacherId);
    List<CourseDTO> getCoursesByTeacherUsername(String username);
    void addTeacherToCourse(Long courseId, Long teacherId);
    void removeTeacherFromCourse(Long courseId, Long teacherId);
    List<CourseDTO> getCoursesByStudentId(Long studentId);
    void addStudentToCourse(Long courseId, Long studentId);
    void removeStudentFromCourse(Long courseId, Long studentId);
    boolean existsByCode(String code);
    Integer getCourseCount();
}
