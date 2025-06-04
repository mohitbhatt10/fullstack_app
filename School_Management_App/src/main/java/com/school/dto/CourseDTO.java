package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.Set;

@Data
@NoArgsConstructor
public class CourseDTO {
    private Long id;

    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Course code is required")
    private String code;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    private Integer semester;    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Teacher is required")
    private Long teacherId;

    // Display-only field
    private String teacherName;
    private Set<Long> studentIds;
    private String sessionName;

    // Display-only fields for UI
    private int studentCount;

    @NotNull(message = "Session is required")
    private Long sessionId;

}
