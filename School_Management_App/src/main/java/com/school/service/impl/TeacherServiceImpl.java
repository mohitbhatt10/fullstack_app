package com.school.service.impl;

import com.school.dto.TeacherDTO;
import com.school.entity.Teacher;
import com.school.entity.UserRole;
import com.school.repository.TeacherRepository;
import com.school.service.TeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherServiceImpl(TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDTO, teacher);
        teacher.setRole(UserRole.TEACHER);
        teacher.setPassword(passwordEncoder.encode(teacherDTO.getPassword()));
        Teacher savedTeacher = teacherRepository.save(teacher);
        TeacherDTO savedDTO = new TeacherDTO();
        BeanUtils.copyProperties(savedTeacher, savedDTO);
        return savedDTO;
    }

    @Override
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        BeanUtils.copyProperties(teacherDTO, teacher, "id", "password", "role");
        Teacher updatedTeacher = teacherRepository.save(teacher);
        TeacherDTO updatedDTO = new TeacherDTO();
        BeanUtils.copyProperties(updatedTeacher, updatedDTO);
        return updatedDTO;
    }

    @Override
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    @Override
    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        TeacherDTO teacherDTO = new TeacherDTO();
        BeanUtils.copyProperties(teacher, teacherDTO);
        return teacherDTO;
    }

    @Override
    public TeacherDTO getTeacherByUsername(String username) {
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        TeacherDTO teacherDTO = new TeacherDTO();
        BeanUtils.copyProperties(teacher, teacherDTO);
        return teacherDTO;
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacher -> {
                    TeacherDTO dto = new TeacherDTO();
                    BeanUtils.copyProperties(teacher, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherDTO> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department).stream()
                .map(teacher -> {
                    TeacherDTO dto = new TeacherDTO();
                    BeanUtils.copyProperties(teacher, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
