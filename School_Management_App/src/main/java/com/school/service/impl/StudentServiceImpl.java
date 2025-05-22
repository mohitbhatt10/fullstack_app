package com.school.service.impl;

import com.school.dto.StudentDTO;
import com.school.entity.Student;
import com.school.entity.Course;
import com.school.entity.UserRole;
import com.school.repository.StudentRepository;
import com.school.repository.CourseRepository;
import com.school.service.StudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, 
                            CourseRepository courseRepository,
                            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        // Create and save the new student
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        student.setRole(UserRole.STUDENT);
        student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        Student savedStudent = studentRepository.save(student);        // Find and enroll in all courses for the student's semester and department
        List<Course> courses = courseRepository.findBySemesterAndDepartment(student.getSemester(), student.getDepartment());
        for (Course course : courses) {
            if (course.getStudents() == null) {
                course.setStudents(new HashSet<>());
            }
            course.getStudents().add(savedStudent);
            courseRepository.save(course);
        }

        // Return the student DTO
        StudentDTO savedDTO = new StudentDTO();
        BeanUtils.copyProperties(savedStudent, savedDTO);
        return savedDTO;
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        BeanUtils.copyProperties(studentDTO, student, "id", "password", "role");
        Student updatedStudent = studentRepository.save(student);
        StudentDTO updatedDTO = new StudentDTO();
        BeanUtils.copyProperties(updatedStudent, updatedDTO);
        return updatedDTO;
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student, studentDTO);
        return studentDTO;
    }

    @Override
    public StudentDTO getStudentByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student, studentDTO);
        return studentDTO;
    }

    @Override
    public StudentDTO getStudentByUsername(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student, studentDTO);
        return studentDTO;
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    StudentDTO dto = new StudentDTO();
                    BeanUtils.copyProperties(student, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsBySemester(Integer semester) {
        return studentRepository.findBySemester(semester).stream()
                .map(student -> {
                    StudentDTO dto = new StudentDTO();
                    BeanUtils.copyProperties(student, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department).stream()
                .map(student -> {
                    StudentDTO dto = new StudentDTO();
                    BeanUtils.copyProperties(student, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getStudents().stream()
                .map(student -> {
                    StudentDTO dto = new StudentDTO();
                    BeanUtils.copyProperties(student, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByRollNumber(String rollNumber) {
        return studentRepository.existsByRollNumber(rollNumber);
    }

    @Override
    public Page<StudentDTO> getStudentsByFilters(String department, 
                                               Integer semester, 
                                               String name, 
                                               Pageable pageable) {
        return studentRepository.findByFilters(department, semester, name, pageable)
                .map(student -> {
                    StudentDTO dto = new StudentDTO();
                    BeanUtils.copyProperties(student, dto);
                    return dto;
                });
    }
}
