-- Create exam_types table
CREATE TABLE exam_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT DEFAULT 0
);

-- Insert default exam types
INSERT INTO exam_types (name, description, active, display_order) VALUES
('MID_TERM', 'Mid Term Examination', TRUE, 1),
('FINAL', 'Final Examination', TRUE, 2),
('ASSIGNMENT', 'Assignment', TRUE, 3),
('QUIZ', 'Quiz', TRUE, 4);

-- Add exam_type_id column to marks table
ALTER TABLE marks ADD COLUMN exam_type_id BIGINT;

-- Update existing marks to use the new exam_type_id based on the old exam_type string
UPDATE marks SET exam_type_id = (
    SELECT id FROM exam_types WHERE name = marks.exam_type
) WHERE exam_type IS NOT NULL;

-- Add foreign key constraint
ALTER TABLE marks ADD CONSTRAINT fk_marks_exam_type 
    FOREIGN KEY (exam_type_id) REFERENCES exam_types(id);

-- Make exam_type_id NOT NULL after data migration
ALTER TABLE marks MODIFY COLUMN exam_type_id BIGINT NOT NULL;

-- Drop the old exam_type column
ALTER TABLE marks DROP COLUMN exam_type;