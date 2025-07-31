-- Create feature_switches table
CREATE TABLE feature_switches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_key VARCHAR(100) NOT NULL UNIQUE,
    feature_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    updated_by VARCHAR(100) NULL,
    INDEX idx_feature_key (feature_key),
    INDEX idx_enabled (enabled)
);

-- Insert default feature switches
INSERT INTO feature_switches (feature_key, feature_name, description, enabled) VALUES
('TWO_FACTOR_AUTH', 'Two-Factor Authentication', 'Enable SMS-based two-factor authentication for enhanced security', FALSE),
('EMAIL_NOTIFICATIONS', 'Email Notifications', 'Enable email notifications for system events', TRUE),
('SMS_NOTIFICATIONS', 'SMS Notifications', 'Enable SMS notifications for system events', FALSE);
