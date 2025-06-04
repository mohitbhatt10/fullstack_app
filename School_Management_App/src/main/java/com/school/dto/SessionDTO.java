package com.school.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class SessionDTO {
    private Long id;

    @NotBlank(message = "Session name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private boolean active;

    // Count fields for UI display
    private Integer courseCount;
    private Integer studentCount;
}
