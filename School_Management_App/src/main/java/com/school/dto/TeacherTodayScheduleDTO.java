package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTodayScheduleDTO {
    private Long scheduleId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String classroom;
    private LocalTime startTime;
    private LocalTime endTime;
    private String scheduleInfo;
    private boolean hasAttendance;
    private LocalDate date; // typically today
    private Integer presentCount;
    private Integer totalCount;
}
