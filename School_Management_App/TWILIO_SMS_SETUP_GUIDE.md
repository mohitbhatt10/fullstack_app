# Twilio SMS Integration Guide

This document explains how to set up and configure Twilio SMS integration for the School Management System's Two-Factor Authentication feature.

## 📋 Prerequisites

1. **Twilio Account**: Sign up at [https://www.twilio.com](https://www.twilio.com)
2. **Phone Number**: Purchase a Twilio phone number for sending SMS
3. **Account Credentials**: Obtain your Account SID and Auth Token

## 🔧 Twilio Setup Steps

### 1. Create Twilio Account
1. Go to [Twilio Console](https://console.twilio.com/)
2. Sign up or log in to your account
3. Complete phone verification

### 2. Get Account Credentials
1. From the Twilio Console Dashboard, note down:
   - **Account SID** (starts with 'AC')
   - **Auth Token** (click to reveal)

### 3. Purchase Phone Number
1. Go to **Phone Numbers** → **Manage** → **Buy a number**
2. Choose a phone number that supports SMS
3. Purchase the number

## ⚙️ Configuration

### 1. Environment Variables (Recommended)
Set the following environment variables:

```bash
# Windows
set TWILIO_ACCOUNT_SID=your_actual_account_sid
set TWILIO_AUTH_TOKEN=your_actual_auth_token
set TWILIO_PHONE_NUMBER=+1234567890
set TWILIO_ENABLED=true

# Linux/Mac
export TWILIO_ACCOUNT_SID=your_actual_account_sid
export TWILIO_AUTH_TOKEN=your_actual_auth_token
export TWILIO_PHONE_NUMBER=+1234567890
export TWILIO_ENABLED=true
```

### 2. Application Properties (Alternative)
Update `application.properties`:

```properties
# Twilio SMS Configuration
twilio.account.sid=your_actual_account_sid
twilio.auth.token=your_actual_auth_token
twilio.phone.number=+1234567890
twilio.enabled=true
```

⚠️ **Security Note**: Never commit real credentials to version control. Use environment variables or external configuration.

## 📱 Phone Number Format

The system automatically formats phone numbers to international format:

- **US Numbers**: `1234567890` → `+11234567890`
- **International**: `+447123456789` (UK format)
- **With Country Code**: `1234567890` → `+11234567890`

## 🧪 Testing

### 1. Enable Feature Switch
1. Log in as admin
2. Go to **Feature Management**
3. Enable **Two-Factor Authentication**

### 2. Add Phone Number to User
1. Update user profile with valid phone number
2. Ensure number includes country code

### 3. Test Login Flow
1. Login with user that has phone number
2. System should send OTP via SMS
3. Enter received OTP to complete login

## 🛠️ Development Mode

For development/testing without Twilio:

```properties
twilio.enabled=false
```

When disabled, OTP codes are logged to console instead of sent via SMS.

## 📊 Monitoring and Logs

### Application Logs
```
INFO  - SMS sent successfully to: ****1234 with SID: SM123456789
WARN  - Failed to send SMS to: ****1234
ERROR - Twilio error sending SMS to ****1234: Invalid phone number
```

### Twilio Console
Monitor SMS delivery and costs in the Twilio Console:
- **Logs** → **Messaging** for delivery status
- **Usage** for cost tracking

## 🔒 Security Features

1. **Phone Number Masking**: Only last 4 digits shown in logs
2. **Message Template**: Includes security warning
3. **Rate Limiting**: OTP generation has built-in limits
4. **Expiration**: OTPs expire after 5 minutes

## 💰 Cost Considerations

- **SMS Cost**: ~$0.0075 per SMS in US
- **Phone Number**: ~$1/month rental
- **International**: Varies by country

## 🚨 Error Handling

The system handles various error scenarios:

1. **Invalid Phone Number**: Logs error, continues without SMS
2. **Twilio Service Down**: Logs error, user can request resend
3. **Missing Configuration**: Falls back to mock mode
4. **Rate Limits**: Twilio enforces sending limits

## 🔧 Troubleshooting

### Common Issues

1. **SMS Not Received**
   - Check phone number format
   - Verify Twilio balance
   - Check spam/blocked messages

2. **Invalid Credentials**
   - Verify Account SID and Auth Token
   - Check environment variables

3. **Phone Number Issues**
   - Ensure number is SMS-capable
   - Verify international format

### Debug Steps

1. Check application logs for errors
2. Verify Twilio Console for delivery status
3. Test with different phone numbers
4. Confirm environment variables

## 📚 Additional Resources

- [Twilio SMS API Documentation](https://www.twilio.com/docs/sms)
- [Phone Number Formatting Guide](https://www.twilio.com/docs/glossary/what-e164)
- [Twilio Console](https://console.twilio.com)

## 🔄 Fallback Options

If Twilio is unavailable, consider these alternatives:

1. **AWS SNS**: Alternative SMS service
2. **Email OTP**: Send OTP via email instead
3. **App-based**: Use authenticator apps
4. **SMS Gateway**: Local SMS provider

---

**Note**: Always test SMS integration thoroughly before production deployment. Consider implementing multiple fallback mechanisms for critical authentication flows.
