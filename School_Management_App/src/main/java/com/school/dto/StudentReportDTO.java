package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportDTO {
    private Long id;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private Integer semester;
    private LocalDateTime createdAt;
    
    // Academic performance data
    private Double overallAverage;
    private Double attendancePercentage;
    private Integer totalCourses;
    private Integer passedCourses;
    private Integer failedCourses;
    
    // Course-wise details
    private List<CoursePerformanceDTO> coursePerformances;
    
    // Additional metrics
    private String performanceGrade;
    private String attendanceStatus;
    private Integer totalClassesAttended;
    private Integer totalClassesHeld;
}