package com.school.repository;

import com.school.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudentId(Long studentId);
    List<Mark> findByCourseId(Long courseId);
    List<Mark> findBySemester(Integer semester);
    List<Mark> findByStudentIdAndSemester(Long studentId, Integer semester);
    List<Mark> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Mark> findByExamTypeId(Long examTypeId);
    
    @Query("SELECT m FROM Mark m WHERE m.examType.name = :examTypeName")
    List<Mark> findByExamTypeName(@Param("examTypeName") String examTypeName);
    
    @Query("SELECT m FROM Mark m JOIN m.course c JOIN c.students s WHERE s.id = :studentId")
    List<Mark> findByCourseStudentId(@Param("studentId") Long studentId);

    List<Mark> findByCourseSemester(Integer semester);
    
    @Query("SELECT m FROM Mark m WHERE m.enteredByTeacher.username = :username ORDER BY m.semester DESC")
    List<Mark> findByEnteredByTeacherUsername(@Param("username") String username);

    @Query("SELECT m FROM Mark m WHERE m.course.id = :courseId AND m.examType.id = :examTypeId")
    List<Mark> findByCourseIdAndExamTypeId(@Param("courseId") Long courseId, @Param("examTypeId") Long examTypeId);
}
