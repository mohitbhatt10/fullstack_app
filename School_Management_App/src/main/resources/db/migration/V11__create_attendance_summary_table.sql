CREATE TABLE attendance_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    date DATE NOT NULL,
    present_count INT NOT NULL,
    total_count INT NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    teacher_id BIGINT NOT NULL,
    FOREIGN KEY (schedule_id) REFERENCES course_schedules(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    UNIQUE KEY uk_schedule_date (schedule_id, date)
); 