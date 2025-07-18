package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherReportDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String designation;
    private String specialization;
    private LocalDateTime createdAt;
    
    // Teaching load
    private Integer totalCourses;
    private Integer totalStudents;
    private Integer totalClasses;
    
    // Course details
    private List<TeacherCourseDTO> courses;
    
    // Performance metrics
    private Double averageClassAttendance;
    private Double averageStudentPerformance;
    private Integer marksEntered;
    private Integer attendanceMarked;
}