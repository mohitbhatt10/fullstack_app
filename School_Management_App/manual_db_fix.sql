-- Manual database fix for exam_type migration
-- Run this script directly on your MySQL database

-- First, check if the old exam_type column exists
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'school_management' 
AND TABLE_NAME = 'marks' 
AND COLUMN_NAME = 'exam_type';

-- If the column exists, drop it
ALTER TABLE marks DROP COLUMN IF EXISTS exam_type;

-- Verify the table structure
DESCRIBE marks;