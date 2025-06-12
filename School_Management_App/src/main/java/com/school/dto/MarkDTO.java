package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
public class MarkDTO {
    private Long id;

    @NotNull(message = "Student is required")
    private Long studentId;

    @NotNull(message = "Course is required")
    private Long courseId;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    private Integer semester;

    @NotNull(message = "Exam type is required")
    private Long examTypeId;
    
    // Display-only field for exam type name
    private String examTypeName;

    @NotNull(message = "Marks are required")
    @DecimalMin(value = "0.0", message = "Marks cannot be negative")
    private Double marks;

    @NotNull(message = "Maximum marks are required")
    @DecimalMin(value = "0.0", message = "Maximum marks cannot be negative")
    private Double maxMarks;

    @NotNull(message = "Teacher who entered marks is required")
    private Long enteredByTeacherId;

    // Display-only fields
    private String studentName;
    private String courseName;
    private String teacherName;
    private String remarks;
}
