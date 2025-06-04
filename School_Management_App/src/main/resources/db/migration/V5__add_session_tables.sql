-- Create sessions table
CREATE TABLE sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE
);

-- Insert default session
INSERT INTO sessions (name, start_date, end_date, active) 
VALUES ('2024-2025', '2024-08-01', '2025-05-31', true);

-- Add session_id column to courses table
ALTER TABLE courses ADD COLUMN session_id BIGINT;

-- Set default session_id for existing courses
UPDATE courses SET session_id = (SELECT id FROM sessions WHERE active = true);

-- Make session_id non-nullable
ALTER TABLE courses MODIFY COLUMN session_id BIGINT NOT NULL;

-- Add foreign key constraint
ALTER TABLE courses ADD CONSTRAINT fk_courses_session 
FOREIGN KEY (session_id) REFERENCES sessions(id);

-- Create course_enrollments table
CREATE TABLE course_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    withdrawal_date DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (session_id) REFERENCES sessions(id)
);

-- Migrate existing course-student relationships to course_enrollments
INSERT INTO course_enrollments (student_id, course_id, session_id, enrollment_date, active)
SELECT cs.student_id, cs.course_id, c.session_id, CURRENT_DATE(), true
FROM course_students cs
JOIN courses c ON cs.course_id = c.id;

-- Keep the original course_students table for now as a backup
-- Can be removed in a future migration once we're confident everything works
