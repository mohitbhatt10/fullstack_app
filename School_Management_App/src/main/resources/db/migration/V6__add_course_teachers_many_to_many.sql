-- Create course_teachers junction table for Many-to-Many relationship
CREATE TABLE course_teachers (
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    PRIMARY KEY (course_id, teacher_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
);

-- Migrate existing course-teacher relationships from courses.teacher_id to course_teachers table
INSERT INTO course_teachers (course_id, teacher_id)
SELECT id, teacher_id 
FROM courses 
WHERE teacher_id IS NOT NULL;

-- Remove the old teacher_id column from courses table
-- Note: We'll do this in steps to ensure data integrity

-- First, create a backup of the teacher_id column (optional, for safety)
ALTER TABLE courses ADD COLUMN teacher_id_backup BIGINT;
UPDATE courses SET teacher_id_backup = teacher_id;

-- Now remove the foreign key constraint
ALTER TABLE courses DROP FOREIGN KEY courses_ibfk_1;

-- Drop the teacher_id column
ALTER TABLE courses DROP COLUMN teacher_id;

-- The teacher_id_backup column can be removed in a future migration once we're confident everything works
-- ALTER TABLE courses DROP COLUMN teacher_id_backup;