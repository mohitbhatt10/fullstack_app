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

    @Override
    public Integer getTeacherCount() {
        return (int) teacherRepository.count();
    }

    @Override
    public List<TeacherDTO> importTeachersFromExcel(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        List<TeacherDTO> importedTeachers = new java.util.ArrayList<>();
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(file.getInputStream())) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            for (org.apache.poi.ss.usermodel.Row row : sheet) {
                if (isHeader) {
                    isHeader = false;
                    continue; // skip header
                }
                if (row == null || row.getCell(0) == null) continue;
                TeacherDTO teacherDTO = new TeacherDTO();
                teacherDTO.setFirstName(getCellString(row, 0));
                teacherDTO.setLastName(getCellString(row, 1));
                teacherDTO.setEmail(getCellString(row, 2));
                teacherDTO.setDepartment(getCellString(row, 3));
                teacherDTO.setDesignation(getCellString(row, 4));
                teacherDTO.setSpecialization(getCellString(row, 5));
                teacherDTO.setPassword(row.getCell(6) != null ? getCellString(row, 6) : "password123"); // default password if not provided
                teacherDTO.setUsername(getCellString(row, 7));
                importedTeachers.add(createTeacher(teacherDTO));
            }
        }
        return importedTeachers;
    }

    private String getCellString(org.apache.poi.ss.usermodel.Row row, int cellNum) {
        org.apache.poi.ss.usermodel.Cell cell = row.getCell(cellNum);
        if (cell == null) return "";
        cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    @Override
    public Teacher findByUsername(String username) {
        return teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }
}
