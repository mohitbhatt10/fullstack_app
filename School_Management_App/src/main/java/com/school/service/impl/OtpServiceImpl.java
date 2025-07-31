package com.school.service.impl;

import com.school.entity.OtpVerification;
import com.school.repository.OtpVerificationRepository;
import com.school.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public String generateAndSendOtp(String phoneNumber, OtpVerification.OtpType otpType) {
        log.info("Generating OTP for phone number: {}", phoneNumber);
        
        // Generate 6-digit OTP
        String otpCode = generateOtpCode();
        
        // Create OTP verification record
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setPhoneNumber(phoneNumber);
        otpVerification.setOtpCode(otpCode);
        otpVerification.setOtpType(otpType);
        
        // Save to database
        otpRepository.save(otpVerification);
        
        // Send SMS (simulate for now)
        sendSms(phoneNumber, otpCode);
        
        log.info("OTP generated and sent to: {}", phoneNumber);
        return otpCode; // Return for testing purposes (remove in production)
    }

    @Override
    @Transactional
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        log.info("Verifying OTP for phone number: {}", phoneNumber);
        
        Optional<OtpVerification> otpOpt = otpRepository.findByPhoneNumberAndOtpCodeAndVerifiedFalse(phoneNumber, otpCode);
        
        if (otpOpt.isEmpty()) {
            log.warn("OTP not found for phone: {} and code: {}", phoneNumber, otpCode);
            return false;
        }
        
        OtpVerification otp = otpOpt.get();
        
        // Check if OTP is expired
        if (otp.isExpired()) {
            log.warn("OTP expired for phone: {}", phoneNumber);
            return false;
        }
        
        // Check if max attempts reached
        if (otp.isMaxAttemptsReached()) {
            log.warn("Max attempts reached for OTP verification for phone: {}", phoneNumber);
            return false;
        }
        
        // Mark as verified
        otpRepository.markAsVerified(otp.getId(), LocalDateTime.now());
        log.info("OTP verified successfully for phone: {}", phoneNumber);
        return true;
    }

    @Override
    @Transactional
    public String createVerificationSession(String phoneNumber) {
        log.info("Creating verification session for phone: {}", phoneNumber);
        
        // Find the latest verified OTP for this phone number
        Optional<OtpVerification> latestOtpOpt = otpRepository.findLatestUnverifiedOtp(phoneNumber, LocalDateTime.now());
        
        if (latestOtpOpt.isEmpty()) {
            throw new RuntimeException("No verified OTP found for phone number");
        }
        
        // Generate session token
        String sessionToken = UUID.randomUUID().toString();
        
        OtpVerification otp = latestOtpOpt.get();
        otp.setSessionToken(sessionToken);
        otpRepository.save(otp);
        
        log.info("Verification session created for phone: {}", phoneNumber);
        return sessionToken;
    }

    @Override
    public boolean isSessionValid(String sessionToken) {
        Optional<OtpVerification> sessionOpt = otpRepository.findBySessionTokenAndVerifiedTrue(sessionToken);
        
        if (sessionOpt.isEmpty()) {
            return false;
        }
        
        OtpVerification session = sessionOpt.get();
        // Session is valid for 10 minutes after verification
        LocalDateTime sessionExpiry = session.getVerifiedAt().plusMinutes(10);
        
        return LocalDateTime.now().isBefore(sessionExpiry);
    }

    @Override
    @Transactional
    public void cleanupExpiredOtps() {
        log.info("Cleaning up expired OTPs");
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1); // Delete OTPs older than 1 hour
        otpRepository.deleteExpiredOtps(cutoffTime);
        log.info("Expired OTPs cleaned up");
    }

    @Override
    public OtpVerification getLatestUnverifiedOtp(String phoneNumber) {
        return otpRepository.findLatestUnverifiedOtp(phoneNumber, LocalDateTime.now()).orElse(null);
    }

    private String generateOtpCode() {
        // Generate 6-digit OTP
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendSms(String phoneNumber, String otpCode) {
        // TODO: Integrate with actual SMS service (Twilio, AWS SNS, etc.)
        // For now, just log the SMS content
        String message = String.format("Your School Management System verification code is: %s. This code will expire in 5 minutes.", otpCode);
        log.info("SMS sent to {}: {}", phoneNumber, message);
        
        // In production, replace this with actual SMS service integration:
        // smsService.sendSms(phoneNumber, message);
    }
}
