CREATE TABLE marks_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    exam_type_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    max_marks DOUBLE NOT NULL,
    average_marks DOUBLE NOT NULL,
    total_students INT NOT NULL,
    passed_students INT NOT NULL,
    pass_percentage DOUBLE NOT NULL,
    highest_marks DOUBLE NOT NULL,
    lowest_marks DOUBLE NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (exam_type_id) REFERENCES exam_types(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    UNIQUE KEY uk_course_exam_type (course_id, exam_type_id)
); 