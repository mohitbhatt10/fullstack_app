-- Remove the old exam_type column from marks table
-- This migration handles the cleanup after the exam_types table implementation

-- First, check if the column exists and drop it
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'marks' 
     AND COLUMN_NAME = 'exam_type') > 0,
    'ALTER TABLE marks DROP COLUMN exam_type',
    'SELECT "Column exam_type already removed" as message'
));

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;