package com.school.service;

public interface SmsService {
    
    /**
     * Send SMS message to the specified phone number
     * @param phoneNumber Recipient phone number
     * @param message SMS message content
     * @return true if SMS sent successfully, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);
    
    /**
     * Check if SMS service is enabled and configured
     * @return true if SMS service is available
     */
    boolean isSmsEnabled();
    
    /**
     * Format phone number to international format
     * @param phoneNumber Phone number to format
     * @return Formatted phone number
     */
    String formatPhoneNumber(String phoneNumber);
}
