package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyAttendanceDTO {
    private LocalDate date;
    private Integer totalStudents;
    private Integer presentStudents;
    private Integer absentStudents;
    private Double attendancePercentage;
}