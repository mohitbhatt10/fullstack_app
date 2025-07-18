package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamMarkDTO {
    private String examType;
    private Double marks;
    private Double maxMarks;
    private Double percentage;
    private String grade;
}