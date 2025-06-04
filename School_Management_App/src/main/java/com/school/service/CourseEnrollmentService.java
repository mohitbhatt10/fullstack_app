package com.school.service;

import com.school.dto.CourseEnrollmentDTO;
import java.util.List;

public interface CourseEnrollmentService {
    List<CourseEnrollmentDTO> getAllEnrollments();
    CourseEnrollmentDTO createEnrollment(CourseEnrollmentDTO enrollmentDTO);
    CourseEnrollmentDTO updateEnrollment(Long id, CourseEnrollmentDTO enrollmentDTO);
    void deleteEnrollment(Long id);
    CourseEnrollmentDTO getEnrollmentById(Long id);
    List<CourseEnrollmentDTO> getEnrollmentsByStudentId(Long studentId);
    List<CourseEnrollmentDTO> getEnrollmentsByCourseId(Long courseId);
    List<CourseEnrollmentDTO> getEnrollmentsBySessionId(Long sessionId);
    List<CourseEnrollmentDTO> getActiveEnrollmentsByStudentId(Long studentId);
    List<CourseEnrollmentDTO> getActiveEnrollmentsByCourseId(Long courseId);
    CourseEnrollmentDTO withdrawStudentFromCourse(Long studentId, Long courseId);
    List<CourseEnrollmentDTO> getEnrollmentsByStudentIdAndSessionId(Long studentId, Long sessionId);
}
