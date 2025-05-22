package com.school.repository;

import com.school.entity.Teacher;
import java.util.List;

public interface TeacherRepository extends UserRepository<Teacher> {
    List<Teacher> findByDepartment(String department);
    List<Teacher> findByDesignation(String designation);
}
