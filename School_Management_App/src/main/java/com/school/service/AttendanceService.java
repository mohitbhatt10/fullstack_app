package com.school.service;

import com.school.dto.AttendanceDTO;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    AttendanceDTO createAttendance(AttendanceDTO attendanceDTO);
    AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO);
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

    /**
     * Get the valid time window for marking attendance for a course on a specific date.
     * @param courseId The course ID
     * @param date The date to check
     * @return Map containing startTime and endTime for the attendance window, or null if no class scheduled
     */
    Map<String, LocalTime> getAttendanceTimeWindow(Long courseId, LocalDate date);
}
