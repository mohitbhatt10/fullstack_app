package com.school.dto;

import jakarta.validation.constraints.NotEmpty;

public class PasswordResetDto {

    @NotEmpty(message = "New password is required")
    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$",
    //         message = "Password must be at least 8 characters and include a number, uppercase letter, lowercase letter, and special character")
    private String newPassword;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
