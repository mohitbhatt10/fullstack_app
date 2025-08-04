package com.school.service.impl;

import com.school.config.TwilioConfig;
import com.school.service.SmsService;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final TwilioConfig twilioConfig;

    @PostConstruct
    public void init() {
        if (isSmsEnabled()) {
            try {
                Twilio.init(twilioConfig.getAccount().getSid(), twilioConfig.getAuth().getToken());
                log.info("Twilio SMS service initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Twilio SMS service", e);
            }
        } else {
            log.info("Twilio SMS service is disabled or not configured");
        }
    }

    @Override
    public boolean sendSms(String phoneNumber, String message) {
        if (!isSmsEnabled()) {
            log.warn("SMS service is disabled. Message not sent to: {}", phoneNumber);
            return false;
        }

        if (!StringUtils.hasText(phoneNumber) || !StringUtils.hasText(message)) {
            log.error("Phone number or message is empty");
            return false;
        }

        try {
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
            
            Message twilioMessage = Message.creator(
                new PhoneNumber(formattedPhoneNumber),
                new PhoneNumber(twilioConfig.getPhone().getNumber()),
                message
            ).create();

            log.info("SMS sent successfully to: {} with SID: {}", 
                maskPhoneNumber(formattedPhoneNumber), twilioMessage.getSid());
            
            return true;

        } catch (TwilioException e) {
            log.error("Twilio error sending SMS to {}: {}", 
                maskPhoneNumber(phoneNumber), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error sending SMS to {}: {}", 
                maskPhoneNumber(phoneNumber), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isSmsEnabled() {
        return twilioConfig.isEnabled() && 
               StringUtils.hasText(twilioConfig.getAccount().getSid()) &&
               StringUtils.hasText(twilioConfig.getAuth().getToken()) &&
               StringUtils.hasText(twilioConfig.getPhone().getNumber());
    }

    @Override
    public String formatPhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Remove all non-digit characters
        String cleanNumber = phoneNumber.replaceAll("[^+\\d]", "");

        // If number doesn't start with +, assume it's a US number and add +1
        if (!cleanNumber.startsWith("+")) {
            // If it's a 10-digit number, assume US format
            if (cleanNumber.length() == 10) {
                cleanNumber = "+1" + cleanNumber;
            } 
            // If it's an 11-digit number starting with 1, assume US format
            else if (cleanNumber.length() == 11 && cleanNumber.startsWith("1")) {
                cleanNumber = "+" + cleanNumber;
            }
            // For other formats, you might want to add country-specific logic
            else {
                log.warn("Unable to format phone number: {}. Using as-is.", phoneNumber);
                return phoneNumber;
            }
        }

        return cleanNumber;
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        
        String lastFour = phoneNumber.substring(phoneNumber.length() - 4);
        return "****" + lastFour;
    }
}
