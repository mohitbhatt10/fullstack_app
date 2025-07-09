package com.school.dto;

import com.school.entity.ProfileEditRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequestDTO {

    private Long id;

    private Long userId;
    private String userName;
    private String userRole;

    private String currentUsername;
    private String currentEmail;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    private String requestedUsername;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String requestedEmail;

    @NotBlank(message = "Please provide a reason for this change")
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;

    private ProfileEditRequest.RequestStatus status;

    private Long approvedById;
    private String approvedByName;

    @Size(max = 1000, message = "Admin comments cannot exceed 1000 characters")
    private String adminComments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime processedAt;

    // Helper methods for display
    public String getStatusBadgeClass() {
        if (status == null) return "badge-secondary";
        return switch (status) {
            case PENDING -> "badge-warning";
            case APPROVED -> "badge-success";
            case REJECTED -> "badge-danger";
        };
    }

    public String getStatusDisplayName() {
        if (status == null) return "Unknown";
        return switch (status) {
            case PENDING -> "Pending Review";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
        };
    }

    public boolean isChangingUsername() {
        return requestedUsername != null && !requestedUsername.equals(currentUsername);
    }

    public boolean isChangingEmail() {
        return requestedEmail != null && !requestedEmail.equals(currentEmail);
    }
}
