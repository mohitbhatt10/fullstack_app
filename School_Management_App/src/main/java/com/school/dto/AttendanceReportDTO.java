package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String department;
    private Integer semester;
    private String courseName;
    private String courseCode;
    
    // Summary statistics
    private Integer totalStudents;
    private Integer totalClasses;
    private Double overallAttendancePercentage;
    private Integer totalPresent;
    private Integer totalAbsent;
    
    // Student-wise attendance
    private List<StudentAttendanceDTO> studentAttendances;
    
    // Date-wise attendance summary
    private List<DailyAttendanceDTO> dailyAttendances;
}