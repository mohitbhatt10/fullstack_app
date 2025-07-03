package com.school.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.school.dto.*;
import com.school.service.SessionService;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.service.CourseService;
import com.school.service.StudentService;
import com.school.service.TeacherService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final SessionService sessionService;

    public AdminController(StudentService studentService, TeacherService teacherService, CourseService courseService, SessionService sessionService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.sessionService = sessionService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        logger.debug("Accessing admin dashboard");
        // Get basic stats
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setStudentCount(studentService.getStudentCount());
        stats.setTeacherCount(teacherService.getTeacherCount());
        stats.setCourseCount(courseService.getCourseCount());

        model.addAttribute("stats", stats);

        // Get active session
        try {
            SessionDTO activeSession = sessionService.getActiveSession();
            model.addAttribute("activeSession", activeSession);
        } catch (Exception e) {
            // No active session found, that's okay
        }
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String listStudents(Model model,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) Integer semester,
                             @RequestParam(required = false) String name,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        logger.debug("Fetching filtered list of students with department={}, semester={}, name={}, page={}, size={}", 
                    department, semester, name, page, size);
                    
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<StudentDTO> studentsPage = studentService.getStudentsByFilters(ObjectUtils.isEmpty(department) ? null : department
        		, semester, ObjectUtils.isEmpty(name) ? null : name, pageable);
        
        // Add pagination data to model
        model.addAttribute("students", studentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentsPage.getTotalPages());
        model.addAttribute("totalItems", studentsPage.getTotalElements());
        
        // Add filter values to model
        model.addAttribute("department", department);
        model.addAttribute("semester", semester);
        model.addAttribute("name", name);
        
        // Add available departments and semesters for filter dropdowns
        model.addAttribute("departments", Arrays.asList("CSE", "ECE", "ME", "CE", "EEE", "IT"));
        model.addAttribute("semesters", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        
        return "admin/students/list";
    }
    
    @GetMapping("/students/import")
    public String showImportStudentsForm() {
        logger.debug("Displaying import students form");
        return "admin/students/import";
    }
    
    @PostMapping("/students/import")
    public String importStudents(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "skipHeader", required = false, defaultValue = "true") boolean skipHeader,
                                RedirectAttributes redirectAttributes) {
        logger.info("Importing students from file: {}", file.getOriginalFilename());
        
        // Check if file is empty
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            return "redirect:/admin/students/import";
        }
        
        // Check if file is an Excel file
        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please upload an Excel file (.xlsx or .xls)");
            return "redirect:/admin/students/import";
        }
        
        try {
            // Process the file and import students
            List<StudentDTO> importedStudents = studentService.importStudentsFromExcel(file);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    String.format("Successfully imported %d students", importedStudents.size()));
            return "redirect:/admin/students";
            
        } catch (IOException e) {
            logger.error("Failed to import students", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to import students: " + e.getMessage());
            return "redirect:/admin/students/import";
        }
    }
    
    @GetMapping("/students/template")
    public void downloadStudentTemplate(HttpServletResponse response) throws IOException {
        logger.debug("Downloading student import template");
        
        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=student_import_template.xlsx");
        
        // Create workbook
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"First Name", "Last Name", "Email", "Username", "Password", "Roll Number", "Department", "Semester"};
            
            // Create cell style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            
            // Add headers
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("John");
            sampleRow.createCell(1).setCellValue("Doe");
            sampleRow.createCell(2).setCellValue("john.doe@example.com");
            sampleRow.createCell(3).setCellValue("johndoe");
            sampleRow.createCell(4).setCellValue("password123");
            sampleRow.createCell(5).setCellValue("CSE001");
            sampleRow.createCell(6).setCellValue("CSE");
            sampleRow.createCell(7).setCellValue(1);
            
            // Write workbook to response
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.error("Error creating Excel template", e);
            throw e;
        }
    }

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        logger.debug("Displaying add student form");
        model.addAttribute("student", new StudentDTO());
        return "admin/students/form";
    }

    @PostMapping("/students/add")
    public String addStudent(@Valid @ModelAttribute("student") StudentDTO student,
                           BindingResult result,
                           @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile) {
        if (result.hasErrors()) {
            logger.warn("Validation errors occurred while adding student: {}", result.getAllErrors());
            return "admin/students/form";
        }

        try {
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                student.setProfilePicture(profilePictureFile.getBytes());
                student.setProfilePictureContentType(profilePictureFile.getContentType());
            }
            logger.info("Creating new student: {}", student.getUsername());
            studentService.createStudent(student);
            return "redirect:/admin/students";
        } catch (IOException e) {
            logger.error("Failed to process profile picture", e);
            result.rejectValue("profilePicture", "error.profilePicture", "Failed to process profile picture");
            return "admin/students/form";
        }
    }

    @GetMapping("/teachers")
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "admin/teachers/list";
    }

    @GetMapping("/teachers/add")
    public String showAddTeacherForm(Model model) {
        model.addAttribute("teacher", new TeacherDTO());
        return "admin/teachers/form";
    }

    @PostMapping("/teachers/add")
    public String addTeacher(@Valid @ModelAttribute("teacher") TeacherDTO teacher,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile) {
        if (result.hasErrors()) {
            logger.error("Failed to process teacher data during Add: {}", result.getAllErrors());
            result.rejectValue("teacher", "error.teacher", "Failed to parse teacher data");
            return "admin/teachers/form";
        }
        try {
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                teacher.setProfilePicture(profilePictureFile.getBytes());
                teacher.setProfilePictureContentType(profilePictureFile.getContentType());
            }
            teacherService.createTeacher(teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully");
            return "redirect:/admin/teachers";
        } catch (RuntimeException | IOException ex ) {
            logger.error("Failed to process profile picture or some other runtime error", ex);
            result.rejectValue("email", "error.teacher", ex.getMessage());
            return "admin/teachers/form";
        }
    }

    @GetMapping("/students/{id}/edit")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "admin/students/form";
    }

    @PostMapping("/students/{id}/edit")
    public String updateStudent(@PathVariable Long id,
                              @Valid @ModelAttribute("student") StudentDTO student,
                              BindingResult result,
                              @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile) {
        if (result.hasErrors()) {
            logger.error("Failed to process student data: {}", result.getAllErrors());
            result.rejectValue("student", "error.student", "Failed to parse student data");
            return "admin/students/form";
        }

        try {
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                student.setProfilePicture(profilePictureFile.getBytes());
                student.setProfilePictureContentType(profilePictureFile.getContentType());
            }
            studentService.updateStudent(id, student);
            return "redirect:/admin/students";
        } catch (IOException e) {
            logger.error("Failed to process profile picture", e);
            result.rejectValue("profilePicture", "error.profilePicture", "Failed to process profile picture");
            return "admin/students/form";
        }
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/admin/students";
    }

    @GetMapping("/teachers/{id}/edit")
    public String showEditTeacherForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "admin/teachers/form";
    }

    @PostMapping("/teachers/{id}/edit")
    public String updateTeacher(@PathVariable Long id,
                              @Valid @ModelAttribute("teacher") TeacherDTO teacher,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                                @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile) {
        if (result.hasErrors()) {
            logger.error("Failed to process teacher data: {}", result.getAllErrors());
            result.rejectValue("teacher", "error.teacher", "Failed to parse teacher data");
            return "admin/teachers/form";
        }
        try {
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                teacher.setProfilePicture(profilePictureFile.getBytes());
                teacher.setProfilePictureContentType(profilePictureFile.getContentType());
            }
            teacherService.updateTeacher(id, teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully");
            return "redirect:/admin/teachers";
        } catch (RuntimeException | IOException ex ) {
            logger.error("Failed to process profile picture or some other runtime error", ex);
            result.rejectValue("email", "error.teacher", ex.getMessage());
            return "admin/teachers/form";
        }
    }

    @PostMapping("/teachers/{id}/delete")
    public String deleteTeacher(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher deleted successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/teachers";
    }

    @GetMapping("/teachers/import")
    public String showImportTeachersForm() {
        logger.debug("Displaying import teachers form");
        return "admin/teachers/import";
    }
    
    @PostMapping("/teachers/import")
    public String importTeachers(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "skipHeader", required = false, defaultValue = "true") boolean skipHeader,
                                RedirectAttributes redirectAttributes) {
        logger.info("Importing teachers from file: {}", file.getOriginalFilename());
        
        // Check if a file is empty
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            return "redirect:/admin/teachers/import";
        }
        
        // Check if a file is an Excel file
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please upload an Excel file (.xlsx or .xls)");
            return "redirect:/admin/teachers/import";
        }
        
        try {
            // Process the file and import students
            List<TeacherDTO> importedTeachers =  teacherService.importTeachersFromExcel(file);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    String.format("Successfully imported %d teachers", importedTeachers.size()));
            return "redirect:/admin/teachers";
            
        } catch (IOException e) {
            logger.error("Failed to import teachers", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to import teachers: " + e.getMessage());
            return "redirect:/admin/teachers/import";
        }
    }

    @GetMapping("/teachers/template")
    public void downloadTeachersTemplate(HttpServletResponse response) throws IOException {
        logger.debug("Downloading Teacher import template");

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=teacher_import_template.xlsx");

        // Create workbook
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Teachers");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"First Name", "Last Name", "Email", "Department", "Designation", "Specialization", "Password", "Username"};

            // Create cell style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Add headers
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("Charlie");
            sampleRow.createCell(1).setCellValue("Roots");
            sampleRow.createCell(2).setCellValue("charlieR@example.com");
            sampleRow.createCell(3).setCellValue("CSE");
            sampleRow.createCell(4).setCellValue("Professor");
            sampleRow.createCell(5).setCellValue("Artificial Intelligence");
            sampleRow.createCell(6).setCellValue("Password@123");
            sampleRow.createCell(7).setCellValue("charlieR");

            // Write workbook to response
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.error("Error creating Excel template", e);
            throw e;
        }
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses/list";
    }

    @GetMapping("/courses/add")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("academicSessions", sessionService.getAllSessions());
        return "admin/courses/form";
    }

    @PostMapping("/courses/add")
    public String addCourse(@Valid @ModelAttribute("course") CourseDTO course,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // Validate that at least one teacher is selected
        if (course.getTeacherIds() == null || course.getTeacherIds().isEmpty()) {
            result.rejectValue("teacherIds", "error.course", "At least one teacher must be selected");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherService.getAllTeachers());
            model.addAttribute("academicSessions", sessionService.getAllSessions());
            return "admin/courses/form";
        }
        try {
            courseService.createCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully");
            return "redirect:/admin/courses";
        } catch (RuntimeException ex) {
            result.rejectValue("code", "error.course", ex.getMessage());
            model.addAttribute("teachers", teacherService.getAllTeachers());
            model.addAttribute("academicSessions", sessionService.getAllSessions());
            return "admin/courses/form";
        }
    }

    @GetMapping("/courses/{id}/edit")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("academicSessions", sessionService.getAllSessions());
        return "admin/courses/form";
    }

    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                           @Valid @ModelAttribute("course") CourseDTO course,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Validate that at least one teacher is selected
        if (course.getTeacherIds() == null || course.getTeacherIds().isEmpty()) {
            result.rejectValue("teacherIds", "error.course", "At least one teacher must be selected");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherService.getAllTeachers());
            model.addAttribute("academicSessions", sessionService.getAllSessions());
            return "admin/courses/form";
        }
        try {
            courseService.updateCourse(id, course);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully");
            return "redirect:/admin/courses";
        } catch (RuntimeException ex) {
            result.rejectValue("code", "error.course", ex.getMessage());
            model.addAttribute("teachers", teacherService.getAllTeachers());
            model.addAttribute("academicSessions", sessionService.getAllSessions());
            return "admin/courses/form";
        }
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{courseId}/students/{studentId}/add")
    public String addStudentToCourse(@PathVariable Long courseId,
                                   @PathVariable Long studentId,
                                   RedirectAttributes redirectAttributes) {
        try {
            logger.info("Adding student {} to course {}", studentId, courseId);
            courseService.addStudentToCourse(courseId, studentId);
            redirectAttributes.addFlashAttribute("successMessage", "Student added to course successfully");
        } catch (RuntimeException ex) {
            logger.error("Failed to add student {} to course {}: {}", studentId, courseId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/courses/" + courseId + "/students";
    }

    @PostMapping("/courses/{courseId}/students/{studentId}/remove")
    public String removeStudentFromCourse(@PathVariable Long courseId,
                                        @PathVariable Long studentId,
                                        RedirectAttributes redirectAttributes) {
        try {
            logger.info("Removing student {} from course {}", studentId, courseId);
            courseService.removeStudentFromCourse(courseId, studentId);
            redirectAttributes.addFlashAttribute("successMessage", "Student removed from course successfully");
        } catch (RuntimeException ex) {
            logger.error("Failed to remove student {} from course {}: {}", studentId, courseId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/courses/" + courseId + "/students";
    }

    @PostMapping("/courses/{courseId}/students/bulk-add")
    public String addStudentsToCourse(@PathVariable Long courseId,
                                      @RequestParam("studentIds") List<Long> studentIds,
                                      RedirectAttributes redirectAttributes) {
        try {
            if (studentIds == null || studentIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No students selected");
                return "redirect:/admin/courses/" + courseId + "/students";
            }

            logger.info("Adding {} students to course {}", studentIds.size(), courseId);
            courseService.addStudentsToCourse(courseId, studentIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    studentIds.size() + " student(s) added to course successfully");
        } catch (RuntimeException ex) {
            logger.error("Failed to add students to course {}: {}", courseId, ex.getMessage(), ex);
            // The service already provides detailed error messages including success count
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/courses/" + courseId + "/students";
    }

    @GetMapping("/courses/{id}/students")
    public String listCourseStudents(@PathVariable Long id, Model model) {
        logger.debug("Fetching students for course {}", id);
        CourseDTO course = courseService.getCourseById(id);
        List<StudentDTO> enrolledStudents = studentService.getStudentsByCourseId(id);
        List<StudentDTO> allStudents = studentService.getAllStudents();
          // Filter out already enrolled students and students from different semesters/departments
        List<StudentDTO> availableStudents = allStudents.stream()
                .filter(student -> !enrolledStudents.contains(student))
                .filter(student -> student.getSemester().equals(course.getSemester()))
                .filter(student -> student.getDepartment().equals(course.getDepartment()))
                .collect(Collectors.toList());
        
        logger.debug("Course {} (semester {}) has {} enrolled students and {} available students", 
                    id, course.getSemester(), enrolledStudents.size(), availableStudents.size());
        
        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", enrolledStudents);
        model.addAttribute("availableStudents", availableStudents);
        return "admin/courses/students";
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }
}
