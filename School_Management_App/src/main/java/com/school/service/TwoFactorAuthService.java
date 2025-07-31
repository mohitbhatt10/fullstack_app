package com.school.service;

import com.school.entity.User;

public interface TwoFactorAuthService {
    
    /**
     * Check if 2FA is enabled for the system
     */
    boolean is2FAEnabled();
    
    /**
     * Check if user has phone number configured for 2FA
     */
    boolean isUserEligibleFor2FA(User user);
    
    /**
     * Send OTP to user's phone number
     */
    String sendLoginOtp(User user);
    
    /**
     * Verify OTP and complete 2FA process
     */
    boolean verifyLoginOtp(User user, String otpCode);
    
    /**
     * Create authentication session after successful 2FA
     */
    String createAuthSession(User user);
    
    /**
     * Validate if an auth session is valid
     */
    boolean isAuthSessionValid(String sessionToken);
}
