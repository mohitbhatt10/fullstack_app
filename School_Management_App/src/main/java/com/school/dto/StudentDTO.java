package com.school.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class StudentDTO extends UserDTO {
    @NotBlank(message = "Roll number is required")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Roll number can only contain uppercase letters and numbers")
    private String rollNumber;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    private Integer semester;

    @NotBlank(message = "Department is required")
    private String department;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentDTO)) return false;
        StudentDTO that = (StudentDTO) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
