package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReportDTO {
    private Long id;
    private String name;
    private String code;
    private String department;
    private Integer semester;
    private String syllabus;
    
    // Enrollment statistics
    private Integer totalEnrolledStudents;
    private Integer activeStudents;
    private Integer inactiveStudents;
    
    // Performance statistics
    private Double averageMarks;
    private Double averageAttendance;
    private Integer passedStudents;
    private Integer failedStudents;
    private Double passPercentage;
    
    // Teacher information
    private List<String> teacherNames;
    private List<Long> teacherIds;
    
    // Student performance breakdown
    private List<StudentPerformanceSummaryDTO> studentPerformances;
    
    // Grade distribution
    private Integer gradeA;
    private Integer gradeB;
    private Integer gradeC;
    private Integer gradeD;
    private Integer gradeF;
}