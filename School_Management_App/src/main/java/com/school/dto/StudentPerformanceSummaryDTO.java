package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPerformanceSummaryDTO {
    private Long studentId;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private Double averageMarks;
    private Double attendancePercentage;
    private String grade;
    private String status; // PASS/FAIL/PENDING
}