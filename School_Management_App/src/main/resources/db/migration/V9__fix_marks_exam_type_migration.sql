-- First, let's check if the old exam_type column still exists and handle the migration properly

-- Update existing marks to use the new exam_type_id based on the old exam_type string
-- This handles the case where marks already exist with string exam types
UPDATE marks SET exam_type_id = (
    CASE 
        WHEN exam_type = 'MID_TERM' THEN (SELECT id FROM exam_types WHERE name = 'Mid Term')
        WHEN exam_type = 'FINAL' THEN (SELECT id FROM exam_types WHERE name = 'Final')
        WHEN exam_type = 'ASSIGNMENT' THEN (SELECT id FROM exam_types WHERE name = 'Assignment')
        WHEN exam_type = 'QUIZ' THEN (SELECT id FROM exam_types WHERE name = 'Quiz')
        WHEN exam_type = 'PROJECT' THEN (SELECT id FROM exam_types WHERE name = 'Project')
        WHEN exam_type = 'PRESENTATION' THEN (SELECT id FROM exam_types WHERE name = 'Presentation')
        ELSE (SELECT id FROM exam_types WHERE name = 'Mid Term' LIMIT 1) -- Default fallback
    END
) WHERE exam_type_id IS NULL AND exam_type IS NOT NULL;

-- Now drop the old exam_type column if it still exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'marks' 
     AND COLUMN_NAME = 'exam_type') > 0,
    'ALTER TABLE marks DROP COLUMN exam_type',
    'SELECT "Column exam_type does not exist" as message'
));

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;