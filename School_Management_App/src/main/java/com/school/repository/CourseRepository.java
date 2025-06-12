package com.school.repository;

import com.school.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findBySemester(Integer semester);
    List<Course> findBySemesterAndDepartment(Integer semester, String department);
    List<Course> findByTeachersId(Long teacherId);
    boolean existsByCode(String code);
}
