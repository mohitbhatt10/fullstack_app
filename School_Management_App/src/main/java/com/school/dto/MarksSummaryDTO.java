package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarksSummaryDTO {
    private Long id;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long examTypeId;
    private String examTypeName;
    private Long teacherId;
    private String teacherName;
    private Double maxMarks;
    private Double averageMarks;
    private Integer totalStudents;
    private Integer passedStudents;
    private Double passPercentage;
    private Double highestMarks;
    private Double lowestMarks;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
} 