package com.school.service;

import com.school.dto.AttendanceDTO;
import com.school.dto.AttendanceSummaryDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    AttendanceDTO createAttendance(AttendanceDTO attendanceDTO);
    List<AttendanceDTO> updateAttendance(List<AttendanceDTO> attendanceDTOs);
    void deleteAttendance(Long id);
    AttendanceDTO getAttendanceById(Long id);
    List<AttendanceDTO> getAttendanceByStudentId(Long studentId);
    List<AttendanceDTO> getAttendanceByCourseId(Long courseId);
    List<AttendanceDTO> getAttendanceByStudentIdAndCourseId(Long studentId, Long courseId);
    List<AttendanceDTO> getAttendanceByDate(LocalDate date);
    List<AttendanceDTO> getAttendanceByDateBetween(LocalDate startDate, LocalDate endDate);
    double getAttendancePercentage(Long studentId, Long courseId);
    List<AttendanceDTO> getAllAttendance();
    List<AttendanceDTO> getAttendanceByStudent(Long studentId);
    List<AttendanceDTO> getAttendanceBySemester(Integer semester);
    List<AttendanceDTO> getRecentAttendanceByTeacher(String username);

    // New methods for attendance summary
    List<AttendanceSummaryDTO> getAttendanceSummaryByCourseId(Long courseId);
    List<AttendanceSummaryDTO> getAttendanceSummaryByTeacher(String username);
    List<AttendanceSummaryDTO> getAttendanceSummaryByDateRange(LocalDate startDate, LocalDate endDate);
    AttendanceSummaryDTO getAttendanceSummaryByScheduleAndDate(Long scheduleId, LocalDate date);

    /**
     * Get the valid time window for marking attendance for a course on a specific date.
     * @param courseId The course ID
     * @param date The date to check
     * @return Map containing startTime and endTime for the attendance window, or null if no class scheduled
     */
    Map<String, LocalTime> getAttendanceTimeWindow(Long courseId, LocalDate date);
    
    /**
     * Check if attendance records exist for a specific course, schedule, and date
     * 
     * @param courseId The ID of the course
     * @param scheduleId The ID of the schedule
     * @param date The date to check
     * @return true if attendance records exist, false otherwise
     */
    boolean existsAttendanceForCourseScheduleAndDate(Long courseId, Long scheduleId, LocalDate date);
    
    /**
     * Get attendance records for a specific course, schedule, and date
     * 
     * @param courseId The ID of the course
     * @param scheduleId The ID of the schedule
     * @param date The date to retrieve records for
     * @return List of attendance records
     */
    List<AttendanceDTO> getAttendanceByCourseScheduleAndDate(Long courseId, Long scheduleId, LocalDate date);

    /**
     * Get the last N attendance records for a student
     */
    List<AttendanceDTO> getRecentAttendanceForStudent(Long studentId, int limit);
    
    /**
     * Get the last N attendance records for a teacher by username
     */
    List<AttendanceDTO> getRecentAttendanceForTeacher(String username, int limit);

      /**
     * Get attendance records for a student, optionally filtered by course and month (yyyy-MM)
     */
    List<AttendanceDTO> getAttendanceForStudent(Long studentId, Long courseId, String month);
}
