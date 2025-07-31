package com.school.service.impl;

import com.school.entity.OtpVerification;
import com.school.entity.User;
import com.school.service.FeatureSwitchService;
import com.school.service.OtpService;
import com.school.service.TwoFactorAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

    private final FeatureSwitchService featureSwitchService;
    private final OtpService otpService;

    @Override
    public boolean is2FAEnabled() {
        return featureSwitchService.isFeatureEnabled(FeatureSwitchServiceImpl.TWO_FACTOR_AUTH);
    }

    @Override
    public boolean isUserEligibleFor2FA(User user) {
        // Check if 2FA is enabled and user has a phone number
        return is2FAEnabled() && StringUtils.hasText(user.getPhoneNumber());
    }

    @Override
    public String sendLoginOtp(User user) {
        if (!isUserEligibleFor2FA(user)) {
            throw new RuntimeException("User is not eligible for 2FA");
        }

        log.info("Sending login OTP to user: {}", user.getUsername());
        
        // Generate and send OTP
        String otpCode = otpService.generateAndSendOtp(user.getPhoneNumber(), OtpVerification.OtpType.LOGIN);
        
        log.info("Login OTP sent to user: {}", user.getUsername());
        return otpCode; // Return for testing (remove in production)
    }

    @Override
    public boolean verifyLoginOtp(User user, String otpCode) {
        if (!isUserEligibleFor2FA(user)) {
            return false;
        }

        log.info("Verifying login OTP for user: {}", user.getUsername());
        
        boolean verified = otpService.verifyOtp(user.getPhoneNumber(), otpCode);
        
        if (verified) {
            log.info("Login OTP verified successfully for user: {}", user.getUsername());
        } else {
            log.warn("Login OTP verification failed for user: {}", user.getUsername());
        }
        
        return verified;
    }

    @Override
    public String createAuthSession(User user) {
        if (!isUserEligibleFor2FA(user)) {
            throw new RuntimeException("User is not eligible for 2FA");
        }

        log.info("Creating auth session for user: {}", user.getUsername());
        
        String sessionToken = otpService.createVerificationSession(user.getPhoneNumber());
        
        log.info("Auth session created for user: {}", user.getUsername());
        return sessionToken;
    }

    @Override
    public boolean isAuthSessionValid(String sessionToken) {
        return otpService.isSessionValid(sessionToken);
    }
}
