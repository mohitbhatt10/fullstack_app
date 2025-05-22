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
}
