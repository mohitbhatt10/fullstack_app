package com.school.service;

import com.school.dto.TeacherDTO;
import java.util.List;

public interface TeacherService {
    TeacherDTO createTeacher(TeacherDTO teacherDTO);
    TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO);
    void deleteTeacher(Long id);
    TeacherDTO getTeacherById(Long id);
    TeacherDTO getTeacherByUsername(String username);
    List<TeacherDTO> getAllTeachers();
    List<TeacherDTO> getTeachersByDepartment(String department);
}
