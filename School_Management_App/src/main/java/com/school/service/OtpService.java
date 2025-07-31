package com.school.service;

import com.school.entity.OtpVerification;

public interface OtpService {
    
    /**
     * Generate and send OTP to the given phone number
     */
    String generateAndSendOtp(String phoneNumber, OtpVerification.OtpType otpType);
    
    /**
     * Verify the OTP code for the given phone number
     */
    boolean verifyOtp(String phoneNumber, String otpCode);
    
    /**
     * Create a session token after successful OTP verification
     */
    String createVerificationSession(String phoneNumber);
    
    /**
     * Validate if a session token is valid and verified
     */
    boolean isSessionValid(String sessionToken);
    
    /**
     * Clean up expired OTPs
     */
    void cleanupExpiredOtps();
    
    /**
     * Get the latest unverified OTP for a phone number
     */
    OtpVerification getLatestUnverifiedOtp(String phoneNumber);
}
