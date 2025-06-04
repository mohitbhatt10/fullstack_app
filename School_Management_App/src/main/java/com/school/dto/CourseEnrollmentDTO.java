package com.school.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class CourseEnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private Long sessionId;
    private String sessionName;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private boolean active;

    // Additional fields for display
    private String studentRollNumber;
    private String courseCode;
}
