package com.school.repository;

import com.school.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByStudentIdAndActive(Long studentId, boolean active);
    List<CourseEnrollment> findByCourseIdAndActive(Long courseId, boolean active);
    List<CourseEnrollment> findBySessionIdAndActive(Long sessionId, boolean active);
    List<CourseEnrollment> findByStudentIdAndCourseIdAndActive(Long studentId, Long courseId, boolean active);

    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.student.id = :studentId AND ce.course.id = :courseId AND ce.session.id = :sessionId AND ce.active = true")
    List<CourseEnrollment> findByStudentIdAndCourseIdAndSessionIdAndActive(
            @Param("studentId") Long studentId, 
            @Param("courseId") Long courseId, 
            @Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.active = true")
    Integer countActiveStudentsByCourseId(@Param("courseId") Long courseId);
}
