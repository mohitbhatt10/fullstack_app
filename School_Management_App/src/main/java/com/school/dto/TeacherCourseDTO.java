package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Integer semester;
    private String department;
    private Integer enrolledStudents;
    private Double averageAttendance;
    private Double averageMarks;
    private Integer totalClasses;
    private Integer classesHeld;
}