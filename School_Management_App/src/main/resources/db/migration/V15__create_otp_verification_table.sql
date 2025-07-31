-- Create otp_verification table
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
    otp_type ENUM('LOGIN', 'PASSWORD_RESET', 'PROFILE_CHANGE') NOT NULL DEFAULT 'LOGIN',
    INDEX idx_phone_number (phone_number),
    INDEX idx_otp_code (otp_code),
    INDEX idx_session_token (session_token),
    INDEX idx_expires_at (expires_at),
    INDEX idx_verified (verified)
);
