package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDTO {
    private Long id;
    private Long scheduleId;
    private String scheduleInfo;  // Display field: e.g., "Monday 09:00-10:30 (Room 101)"
    private String courseName;    // Display field: Course name from schedule
    private String courseCode;    // Display field: Course code from schedule
    private LocalDate date;
    private Integer presentCount;
    private Integer totalCount;
    private Double attendancePercentage;  // Display field: Calculated from presentCount/totalCount
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long teacherId;
    private String teacherName;   // Display field: Teacher's full name
}
