package com.school.service;

import com.school.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentService {
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    StudentDTO getStudentById(Long id);
    StudentDTO getStudentByRollNumber(String rollNumber);
    StudentDTO getStudentByUsername(String username);
    List<StudentDTO> getAllStudents();
    List<StudentDTO> getStudentsBySemester(Integer semester);
    List<StudentDTO> getStudentsByDepartment(String department);
    List<StudentDTO> getStudentsByCourseId(Long courseId);
    boolean existsByRollNumber(String rollNumber);
    
    Page<StudentDTO> getStudentsByFilters(String department, 
                                        Integer semester, 
                                        String name, 
                                        Pageable pageable);
}
