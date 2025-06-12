package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.HashSet;
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

    @NotNull(message = "At least one teacher is required")
    @Size(min = 1, message = "At least one teacher must be selected")
    private Set<Long> teacherIds = new HashSet<>();

    // Display-only field
    private String teacherNames;
    private Set<Long> studentIds;
    private String sessionName;

    // Display-only fields for UI
    private int studentCount;

    @NotNull(message = "Session is required")
    private Long sessionId;

}
