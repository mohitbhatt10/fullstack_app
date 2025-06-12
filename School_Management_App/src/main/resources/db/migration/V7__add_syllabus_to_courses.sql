-- Add syllabus column to courses table
ALTER TABLE courses ADD COLUMN syllabus TEXT;

-- Add comment to describe the column
ALTER TABLE courses MODIFY COLUMN syllabus TEXT COMMENT 'Course syllabus containing learning objectives, topics covered, and course content';