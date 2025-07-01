package com.school.repository;

import com.school.entity.MarksSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarksSummaryRepository extends JpaRepository<MarksSummary, Long> {
    
    @Query("SELECT m FROM MarksSummary m WHERE m.course.id = :courseId AND m.examType.id = :examTypeId")
    Optional<MarksSummary> findByCourseAndExamType(@Param("courseId") Long courseId, @Param("examTypeId") Long examTypeId);

    @Query("SELECT m FROM MarksSummary m WHERE m.teacher.id = :teacherId ORDER BY m.updatedDate DESC")
    List<MarksSummary> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT m FROM MarksSummary m WHERE m.course.id = :courseId ORDER BY m.updatedDate DESC")
    List<MarksSummary> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT m FROM MarksSummary m WHERE m.examType.id = :examTypeId ORDER BY m.updatedDate DESC")
    List<MarksSummary> findByExamTypeId(@Param("examTypeId") Long examTypeId);
} 