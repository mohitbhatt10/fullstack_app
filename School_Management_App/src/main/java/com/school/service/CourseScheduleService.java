package com.school.service;

import com.school.dto.CourseScheduleDTO;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface CourseScheduleService {
    CourseScheduleDTO createSchedule(CourseScheduleDTO scheduleDTO);
    CourseScheduleDTO updateSchedule(Long id, CourseScheduleDTO scheduleDTO);
    void deleteSchedule(Long id);
    CourseScheduleDTO getScheduleById(Long id);
    List<CourseScheduleDTO> getSchedulesByCourseId(Long courseId);
    List<CourseScheduleDTO> getSchedulesByTeacherForDay(Long teacherId, DayOfWeek dayOfWeek);
    boolean isScheduleOverlapping(Long courseId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
    
    /**
     * Check if a schedule would overlap with existing schedules considering semester and session
     * @param semester The semester to check
     * @param sessionId The session ID to check  
     * @param dayOfWeek The day of week
     * @param startTime The start time
     * @param endTime The end time
     * @param excludeScheduleId The schedule ID to exclude from the check (for updates)
     * @return true if there's an overlap, false otherwise
     */
    boolean isScheduleOverlappingBySemesterAndSession(Integer semester, Long sessionId, 
                                                     DayOfWeek dayOfWeek, LocalTime startTime, 
                                                     LocalTime endTime, Long excludeScheduleId);
    
    /**
     * Get all schedules for a specific classroom
     * @param classroom The classroom to get schedules for
     * @return List of schedules for the classroom
     */
    List<CourseScheduleDTO> getSchedulesByClassroom(String classroom);
    
    /**
     * Export the schedule for a course to PDF format
     * @param courseId The ID of the course to export schedule for
     * @throws RuntimeException if export fails
     */
    void exportScheduleToPdf(Long courseId);
    
    /**
     * Get timetable for a student based on their enrolled courses
     * @param studentId The ID of the student
     * @return List of all schedules for the student's courses
     */
    List<CourseScheduleDTO> getTimetableForStudent(Long studentId);
}
