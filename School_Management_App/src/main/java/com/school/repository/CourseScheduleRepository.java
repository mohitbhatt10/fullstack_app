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

    /**
     * Find all schedules for a specific classroom
     * @param classroom The classroom to search for
     * @return List of schedules for the classroom
     */
    List<CourseSchedule> findByClassroom(String classroom);
    
    /**
     * Find all schedules for courses that a student is enrolled in
     * @param studentId The ID of the student
     * @return List of schedules for the student's courses
     */
    @Query("SELECT cs FROM CourseSchedule cs " +
           "WHERE cs.course.id IN (SELECT c.id FROM Course c JOIN c.students s WHERE s.id = :studentId)")
    List<CourseSchedule> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Find overlapping schedules considering semester, session, day of week and time
     * @param semester The semester to check
     * @param sessionId The session ID to check
     * @param dayOfWeek The day of week
     * @param startTime The start time
     * @param endTime The end time
     * @param excludeScheduleId The schedule ID to exclude from the check (for updates)
     * @return List of overlapping schedules
     */
    @Query("SELECT cs FROM CourseSchedule cs " +
           "WHERE cs.course.semester = :semester " +
           "AND cs.course.session.id = :sessionId " +
           "AND cs.dayOfWeek = :dayOfWeek " +
           "AND ((cs.startTime < :endTime AND cs.endTime > :startTime)) " +
           "AND (:excludeScheduleId IS NULL OR cs.id != :excludeScheduleId)")
    List<CourseSchedule> findOverlappingSchedulesBySemesterAndSession(
        @Param("semester") Integer semester,
        @Param("sessionId") Long sessionId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime,
        @Param("excludeScheduleId") Long excludeScheduleId
    );
}
