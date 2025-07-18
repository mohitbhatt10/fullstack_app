package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePerformanceDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String department;
    private Integer semester;
    
    // Performance metrics
    private Double averageMarks;
    private Double attendancePercentage;
    private String grade;
    private String status; // PASS/FAIL/PENDING
    
    // Detailed marks by exam type
    private List<ExamMarkDTO> examMarks;
    
    // Attendance details
    private Integer classesAttended;
    private Integer totalClasses;
    
    // Teacher information
    private List<String> teacherNames;
}