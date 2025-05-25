package com.school.service;

import com.school.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
                                        
    /**
     * Import students from Excel file
     * @param file Excel file containing student data
     * @return List of imported students
     * @throws IOException if file processing fails
     */
    List<StudentDTO> importStudentsFromExcel(MultipartFile file) throws IOException;
}
