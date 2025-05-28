package com.school.repository;

import com.school.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByCourseId(Long courseId);
    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);    @Query("SELECT a FROM Attendance a JOIN a.course c JOIN c.students s WHERE s.id = :studentId")
    List<Attendance> findByCourseStudentId(@Param("studentId") Long studentId);

    List<Attendance> findByCourseSemester(Integer semester);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.teacher.id = :teacherId ORDER BY a.date DESC")
    List<Attendance> findByCourseTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.teacher.username = :username ORDER BY a.date DESC")
    List<Attendance> findByCourseTeacherUsername(@Param("username") String username);
    
    /**
     * Find attendance records for a specific course, schedule, and date
     */
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.schedule.id = :scheduleId AND a.date = :date")
    List<Attendance> findByCourseScheduleAndDate(
        @Param("courseId") Long courseId, 
        @Param("scheduleId") Long scheduleId, 
        @Param("date") LocalDate date);
}
