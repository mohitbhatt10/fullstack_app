package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterDTO {
    private String reportType;
    private String department;
    private Integer semester;
    private Long courseId;
    private Long teacherId;
    private Long studentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String examType;
    private String format; // PDF or EXCEL
    private List<String> columns; // For custom column selection
    private String sortBy;
    private String sortOrder; // ASC or DESC
    
    // Additional filters for specific reports
    private Double minAttendancePercentage;
    private Double maxAttendancePercentage;
    private Double minMarks;
    private Double maxMarks;
    private Boolean includeInactive;
}