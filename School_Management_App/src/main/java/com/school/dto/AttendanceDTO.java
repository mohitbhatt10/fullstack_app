package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AttendanceDTO {
    private Long id;

    @NotNull(message = "Student is required")
    private Long studentId;

    @NotNull(message = "Course is required")
    private Long courseId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Attendance status is required")
    private Boolean present;

    @NotNull(message = "Teacher who marked attendance is required")
    private Long markedByTeacherId;

    private Long scheduleId;

    private String comments;

    @NotNull(message = "Session is required")
    private Long sessionId;

    // Display-only fields
    private String studentName;
    private String courseName;
    private String teacherName;
    private String scheduleInfo;
    private String sessionName;
}
