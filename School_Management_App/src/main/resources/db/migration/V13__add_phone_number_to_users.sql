-- Add phone number field to users table
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);

-- Add unique constraint for phone number
ALTER TABLE users ADD CONSTRAINT uk_users_phone_number UNIQUE (phone_number);

-- Set phone number as NOT NULL after adding default values (will need to be updated manually for existing users)
-- Update existing users with placeholder phone numbers (to be changed by users later)
UPDATE users SET phone_number = CONCAT('1000000', id) WHERE phone_number IS NULL;

-- Now make the column NOT NULL
ALTER TABLE users MODIFY COLUMN phone_number VARCHAR(20) NOT NULL;

-- Add phone number fields to profile_edit_requests table
ALTER TABLE profile_edit_requests ADD COLUMN current_phone_number VARCHAR(20);
ALTER TABLE profile_edit_requests ADD COLUMN requested_phone_number VARCHAR(20);
