package com.school.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private Integer studentCount = 0;
    private Integer teacherCount = 0;
    private Integer courseCount = 0;
    private Integer activeEnrollmentCount = 0;
}
