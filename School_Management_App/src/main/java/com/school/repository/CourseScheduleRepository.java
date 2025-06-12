package com.school.repository;

import com.school.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {
    List<CourseSchedule> findByCourseId(Long courseId);
    
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.course.id = :courseId AND cs.dayOfWeek = :dayOfWeek " +
           "AND ((cs.startTime <= :startTime AND cs.endTime > :startTime) " +
           "OR (cs.startTime < :endTime AND cs.endTime >= :endTime) " +
           "OR (cs.startTime >= :startTime AND cs.endTime <= :endTime))")
    List<CourseSchedule> findOverlappingSchedules(
        @Param("courseId") Long courseId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    @Query("SELECT cs FROM CourseSchedule cs " +
           "WHERE cs.dayOfWeek = :dayOfWeek " +
           "AND :teacherId IN (SELECT t.id FROM cs.course.teachers t)")
    List<CourseSchedule> findByDayOfWeekAndTeacherId(
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("teacherId") Long teacherId
    );
}
