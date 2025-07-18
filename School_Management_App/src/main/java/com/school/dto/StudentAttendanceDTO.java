package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDTO {
    private Long studentId;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private Integer classesAttended;
    private Integer totalClasses;
    private Double attendancePercentage;
    private String attendanceStatus; // EXCELLENT, GOOD, AVERAGE, POOR
}