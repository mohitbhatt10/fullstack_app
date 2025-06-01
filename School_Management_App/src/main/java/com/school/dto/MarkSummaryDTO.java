package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkSummaryDTO {
    private String examType;
    private Double maxMarks;
    private Double averageMarks;
    private int studentCount;
}
