# Two-Factor Authentication (2FA) Feature Documentation

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [User Journey](#user-journey)
- [Admin Management](#admin-management)
- [Configuration](#configuration)
- [Security Considerations](#security-considerations)
- [Troubleshooting](#troubleshooting)

## 🔒 Overview

The Two-Factor Authentication (2FA) feature adds an extra layer of security to the School Management System by requiring users to verify their identity using SMS-based OTP (One-Time Password) codes sent to their registered mobile numbers.

### Key Benefits
- **Enhanced Security**: Protects against password-based attacks
- **Toggleable Feature**: Can be enabled/disabled by administrators
- **User-Friendly**: Seamless integration with existing login flow
- **Mobile-First**: Uses SMS for universal accessibility
- **Audit Trail**: Complete logging and tracking of authentication events

## ✨ Features

### 🔧 Feature Switch System
- **Dynamic Control**: Enable/disable 2FA without code changes
- **Admin Dashboard**: Visual interface for feature management
- **Audit Logging**: Track who enabled/disabled features and when
- **Multiple Features**: Framework supports other toggleable features

### 📱 SMS-Based Authentication
- **6-Digit OTP**: Secure numerical codes
- **Time-Limited**: 5-minute expiration for security
- **Attempt Limiting**: Maximum 3 verification attempts
- **Resend Protection**: 60-second cooldown between requests

### 🎯 Smart User Targeting
- **Automatic Detection**: Only users with phone numbers are eligible
- **Graceful Fallback**: Users without phones bypass 2FA seamlessly
- **Progressive Enhancement**: Works alongside existing authentication

## 🏗️ Architecture

### Component Overview
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Login Form     │───▶│  2FA Check      │───▶│  OTP Verification│
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │ Feature Switch  │    │  SMS Service    │
                       │ Service         │    │ Integration     │
                       └─────────────────┘    └─────────────────┘
```

### Core Services

#### FeatureSwitchService
- Manages feature toggles
- Provides real-time feature status
- Handles enable/disable operations

#### TwoFactorAuthService
- Orchestrates 2FA workflow
- Validates user eligibility
- Manages authentication sessions

#### OtpService
- Generates secure OTP codes
- Handles SMS delivery
- Validates verification attempts

## 🗄️ Database Schema

### Feature Switches Table
```sql
CREATE TABLE feature_switches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_key VARCHAR(100) NOT NULL UNIQUE,
    feature_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    updated_by VARCHAR(100) NULL
);
```

### OTP Verification Table
```sql
CREATE TABLE otp_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 3,
    session_token VARCHAR(255) NULL,
    otp_type ENUM('LOGIN', 'PASSWORD_RESET', 'PROFILE_CHANGE') NOT NULL DEFAULT 'LOGIN'
);
```

## 🚶‍♂️ User Journey

### Normal Login Flow (2FA Disabled)
1. User enters username and password
2. System authenticates credentials
3. User is redirected to appropriate dashboard

### Enhanced Login Flow (2FA Enabled)
1. **Initial Authentication**
   - User enters username and password
   - System validates credentials
   - System checks if user has phone number configured

2. **2FA Eligibility Check**
   - If phone number exists: Proceed to step 3
   - If no phone number: Skip 2FA, login normally

3. **OTP Generation**
   - System generates 6-digit OTP code
   - SMS sent to user's registered phone number
   - User redirected to 2FA verification page

4. **OTP Verification**
   - User enters 6-digit code from SMS
   - System validates code (time, attempts, accuracy)
   - On success: User logged in and redirected to dashboard
   - On failure: Error message displayed, retry allowed

### 2FA Verification Page Features
- **Auto-formatting**: Only accepts numeric input
- **Auto-submission**: Form submits when 6 digits entered
- **Resend functionality**: Request new OTP after 60-second cooldown
- **Visual countdown**: Shows remaining time for resend
- **Masked phone display**: Shows only last 4 digits for privacy

## ⚙️ Admin Management

### Accessing Feature Management
1. **Login as Administrator**
2. **Navigate to Features**
   - URL: `/admin/features`
   - Menu: Admin Dashboard → Feature Management

### Feature Switch Interface
- **Visual Toggle Switches**: Click to enable/disable features
- **Real-time Updates**: Changes applied immediately via AJAX
- **Status Indicators**: Green (enabled) / Gray (disabled)
- **Feature Descriptions**: Detailed explanations of each feature
- **Audit Information**: Shows who last modified and when

### Available Features
- **Two-Factor Authentication**: SMS-based login verification
- **Email Notifications**: System-wide email alerts
- **SMS Notifications**: System-wide SMS alerts

## 🔧 Configuration

### Prerequisites
1. **Phone Numbers**: Users must have phone numbers in their profiles
2. **SMS Service**: Configure SMS provider (currently mocked)

### Enabling 2FA
1. Login as administrator
2. Go to `/admin/features`
3. Toggle "Two-Factor Authentication" switch to ON
4. Feature is immediately active for eligible users

### Disabling 2FA
1. Login as administrator
2. Go to `/admin/features`
3. Toggle "Two-Factor Authentication" switch to OFF
4. All users will bypass 2FA on next login

### SMS Service Integration
The system is prepared for SMS service integration. To connect a real SMS provider:

1. **Update OtpServiceImpl.java**
   ```java
   private void sendSms(String phoneNumber, String otpCode) {
       // Replace this with actual SMS service
       // Example for Twilio:
       // twilioService.sendSms(phoneNumber, message);
   }
   ```

2. **Add SMS Provider Dependencies**
   - Twilio: `com.twilio.sdk:twilio`
   - AWS SNS: `software.amazon.awssdk:sns`

## 🔐 Security Considerations

### OTP Security
- **Time-Limited**: 5-minute expiration
- **Single-Use**: OTP invalidated after successful verification
- **Attempt Limiting**: Maximum 3 attempts per OTP
- **Secure Generation**: Cryptographically secure random numbers

### Session Management
- **Temporary Sessions**: Pre-auth sessions for 2FA flow
- **Session Invalidation**: Automatic cleanup of expired sessions
- **Token-Based**: Secure session tokens for verified users

### Phone Number Protection
- **Masked Display**: Only last 4 digits shown in UI
- **Validation**: Server-side phone number format validation
- **Privacy**: Phone numbers not exposed in logs

## 🛠️ Troubleshooting

### Common Issues

#### "2FA Not Available" Error
- **Cause**: User doesn't have phone number configured
- **Solution**: Add phone number in user profile

#### "Session Expired" Error
- **Cause**: Too much time passed during 2FA flow
- **Solution**: Restart login process

#### "OTP Send Failed" Error
- **Cause**: SMS service integration issue
- **Solution**: Check SMS service configuration and logs

#### "Invalid OTP" Error
- **Causes**: 
  - Incorrect code entered
  - OTP expired (>5 minutes)
  - Maximum attempts exceeded
- **Solution**: Request new OTP code

### Admin Troubleshooting

#### Feature Switch Not Working
1. Check database connection
2. Verify `feature_switches` table exists
3. Check application logs for errors

#### Users Not Receiving SMS
1. Verify SMS service configuration
2. Check phone number format in database
3. Review SMS service logs/quotas

### Development/Testing

#### Testing 2FA Flow
1. Enable 2FA in admin panel
2. Ensure test user has phone number
3. Check application logs for generated OTP codes
4. Use logged OTP for testing (remove in production)

#### Bypassing 2FA (Development Only)
- Temporarily disable via admin panel
- Or remove phone number from test user profile

## 📝 Technical Implementation Notes

### File Structure
```
src/main/java/com/school/
├── entity/
│   ├── FeatureSwitch.java
│   └── OtpVerification.java
├── service/
│   ├── FeatureSwitchService.java
│   ├── TwoFactorAuthService.java
│   └── OtpService.java
├── controller/
│   ├── TwoFactorAuthController.java
│   └── FeatureSwitchController.java
└── config/
    ├── TwoFactorAuthenticationSuccessHandler.java
    └── FeatureSwitchInitializer.java

src/main/resources/
├── templates/auth/
│   └── two-factor.html
├── templates/admin/features/
│   └── list.html
└── db/migration/
    ├── V14__create_feature_switches_table.sql
    └── V15__create_otp_verification_table.sql
```

### API Endpoints
- `GET /auth/2fa` - Display 2FA verification page
- `POST /auth/2fa/verify` - Verify OTP code
- `POST /auth/2fa/resend` - Resend OTP code
- `GET /admin/features` - Feature management page
- `POST /admin/features/{key}/toggle` - Toggle feature switch

### Key Dependencies
- Spring Security: Authentication framework
- Spring Data JPA: Database operations
- Thymeleaf: Template engine
- Bootstrap 5: UI framework
- Font Awesome: Icons

---

## 🎉 Conclusion

The Two-Factor Authentication feature provides enterprise-grade security while maintaining ease of use. The feature switch system ensures flexible deployment and management, making it suitable for production environments where security requirements may vary.

For additional support or feature requests, please contact the development team.
