package com.school.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class CourseScheduleDTO {
    private Long id;

    @NotNull(message = "Course is required")
    private Long courseId;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Classroom is required")
    private String classroom;

    // Display-only fields
    private String courseName;
    private String courseCode;
    private String teacherName;
}
