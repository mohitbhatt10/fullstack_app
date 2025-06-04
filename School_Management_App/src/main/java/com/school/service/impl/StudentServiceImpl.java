package com.school.service.impl;

import com.school.dto.StudentDTO;
import com.school.entity.CourseEnrollment;
import com.school.entity.Student;
import com.school.entity.Course;
import com.school.entity.UserRole;
import com.school.exception.FileProcessingException;
import com.school.repository.CourseEnrollmentRepository;
import com.school.repository.StudentRepository;
import com.school.repository.CourseRepository;
import com.school.service.StudentService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              CourseEnrollmentRepository courseEnrollmentRepository,
                              PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        // Create and save the new student
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        student.setRole(UserRole.STUDENT);
        student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        Student savedStudent = studentRepository.save(student);
        // Find and enroll in all courses for the student's semester and department
        List<Course> courses = courseRepository.findBySemesterAndDepartment(student.getSemester(), student.getDepartment());
        for (Course course : courses) {
            if (course.getStudents() == null) {
                course.setStudents(new HashSet<>());
            }
            course.getStudents().add(savedStudent);
            courseRepository.save(course);

            // Create course enrollment entry
            CourseEnrollment enrollment = new CourseEnrollment();
            enrollment.setStudent(savedStudent);
            enrollment.setCourse(course);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setActive(true);
            enrollment.setSession(course.getSession()); // Assuming course has a session field
            courseEnrollmentRepository.save(enrollment);
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
    
    @Override
    @Transactional
    public List<StudentDTO> importStudentsFromExcel(MultipartFile file) throws IOException {
        logger.info("Importing students from Excel file: {}", file.getOriginalFilename());
        
        List<StudentDTO> importedStudents = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            if (workbook.getNumberOfSheets() == 0) {
                throw new FileProcessingException("The Excel file contains no sheets");
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            
            // Skip the header row (row 0)
            int rowNum = 1;
            int processedRows = 0;
            int skippedRows = 0;
            Row row;
            
            while ((row = sheet.getRow(rowNum)) != null) {
                processedRows++;
                // Skip empty rows
                if (isEmptyRow(row)) {
                    skippedRows++;
                    rowNum++;
                    continue;
                }
                
                try {
                    StudentDTO studentDTO = new StudentDTO();
                    
                    // Get values from the Excel cells
                    String firstName = getCellValueAsString(row, 0, dataFormatter);
                    String lastName = getCellValueAsString(row, 1, dataFormatter);
                    String email = getCellValueAsString(row, 2, dataFormatter);
                    String username = getCellValueAsString(row, 3, dataFormatter);
                    String password = getCellValueAsString(row, 4, dataFormatter);
                    String rollNumber = getCellValueAsString(row, 5, dataFormatter);
                    String department = getCellValueAsString(row, 6, dataFormatter);
                    String semesterStr = getCellValueAsString(row, 7, dataFormatter);
                    
                    // Validate required fields
                    StringBuilder missingFields = new StringBuilder();
                    if (ObjectUtils.isEmpty(firstName)) missingFields.append("First Name, ");
                    if (ObjectUtils.isEmpty(lastName)) missingFields.append("Last Name, ");
                    if (ObjectUtils.isEmpty(email)) missingFields.append("Email, ");
                    if (ObjectUtils.isEmpty(username)) missingFields.append("Username, ");
                    if (ObjectUtils.isEmpty(password)) missingFields.append("Password, ");
                    if (ObjectUtils.isEmpty(rollNumber)) missingFields.append("Roll Number, ");
                    if (ObjectUtils.isEmpty(department)) missingFields.append("Department, ");
                    if (ObjectUtils.isEmpty(semesterStr)) missingFields.append("Semester, ");
                    
                    if (missingFields.length() > 0) {
                        String missingFieldsStr = missingFields.substring(0, missingFields.length() - 2);
                        String errorMsg = String.format("Row %d has missing required fields: %s", rowNum, missingFieldsStr);
                        logger.warn(errorMsg);
                        errors.add(errorMsg);
                        skippedRows++;
                        rowNum++;
                        continue;
                    }
                    
                    // Set student properties
                    studentDTO.setFirstName(firstName);
                    studentDTO.setLastName(lastName);
                    studentDTO.setEmail(email);
                    studentDTO.setUsername(username);
                    studentDTO.setPassword(password);
                    studentDTO.setRollNumber(rollNumber);
                    studentDTO.setDepartment(department);
                    
                    try {
                        Integer semester = Integer.parseInt(semesterStr);
                        if (semester < 1 || semester > 8) {
                            String errorMsg = String.format("Row %d has invalid semester value '%s' (must be between 1 and 8), skipping", rowNum, semesterStr);
                            logger.warn(errorMsg);
                            errors.add(errorMsg);
                            skippedRows++;
                            rowNum++;
                            continue;
                        }
                        studentDTO.setSemester(semester);
                    } catch (NumberFormatException e) {
                        String errorMsg = String.format("Row %d has invalid semester value '%s' (must be a number), skipping", rowNum, semesterStr);
                        logger.warn(errorMsg);
                        errors.add(errorMsg);
                        skippedRows++;
                        rowNum++;
                        continue;
                    }
                    
                    // Create student
                    StudentDTO createdStudent = createStudent(studentDTO);
                    importedStudents.add(createdStudent);
                    logger.debug("Imported student from row {}: {}", rowNum, username);
                    
                } catch (Exception e) {
                    String errorMsg = String.format("Error processing row %d: %s", rowNum, e.getMessage());
                    logger.error(errorMsg, e);
                    errors.add(errorMsg);
                    skippedRows++;
                }
                
                rowNum++;
            }
            
            logger.info("Processed {} rows: {} students imported, {} rows skipped", 
                processedRows, importedStudents.size(), skippedRows);
            
            if (!errors.isEmpty()) {
                logger.warn("Import completed with {} errors", errors.size());
                // If more than half of the rows failed, throw an exception
                if (importedStudents.isEmpty() || errors.size() > importedStudents.size()) {
                    throw new FileProcessingException(String.format(
                        "Import completed with too many errors (%d). First error: %s", 
                        errors.size(), errors.get(0)));
                }
            }
            
            return importedStudents;
            
        } catch (IOException e) {
            logger.error("Failed to import students from Excel", e);
            throw new FileProcessingException("Failed to read Excel file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get cell value as string
     */
    private String getCellValueAsString(Row row, int cellIndex, DataFormatter dataFormatter) {
        Cell cell = row.getCell(cellIndex);
        return cell != null ? dataFormatter.formatCellValue(cell).trim() : "";
    }
    
    /**
     * Check if row is empty
     */
    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }
        
        for (int i = 0; i < 8; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Integer getStudentCount() {
        return (int) studentRepository.count();
    }
}
