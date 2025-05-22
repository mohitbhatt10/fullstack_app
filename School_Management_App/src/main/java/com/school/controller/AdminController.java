package com.school.controller;

import com.school.dto.StudentDTO;
import com.school.dto.TeacherDTO;
import com.school.dto.CourseDTO;
import com.school.service.StudentService;
import com.school.service.TeacherService;
import com.school.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;

    public AdminController(StudentService studentService, TeacherService teacherService, CourseService courseService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        logger.debug("Accessing admin dashboard");
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
        Page<StudentDTO> studentsPage = studentService.getStudentsByFilters(department, semester, name, pageable);
        
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

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        logger.debug("Displaying add student form");
        model.addAttribute("student", new StudentDTO());
        return "admin/students/form";
    }

    @PostMapping("/students/add")
    public String addStudent(@Valid @ModelAttribute("student") StudentDTO student,
                           BindingResult result) {
        if (result.hasErrors()) {
            logger.warn("Validation errors occurred while adding student: {}", result.getAllErrors());
            return "admin/students/form";
        }
        logger.info("Creating new student: {}", student.getUsername());
        studentService.createStudent(student);
        return "redirect:/admin/students";
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
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            logger.warn("Validation errors occurred while adding teacher: {}", result.getAllErrors());
            return "admin/teachers/form";
        }
        try {
            logger.info("Creating new teacher: {}", teacher.getUsername());
            teacherService.createTeacher(teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher added successfully");
            return "redirect:/admin/teachers";
        } catch (RuntimeException ex) {
            logger.error("Failed to create teacher: {}", teacher.getUsername(), ex);
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
                              BindingResult result) {
        if (result.hasErrors()) {
            return "admin/students/form";
        }
        studentService.updateStudent(id, student);
        return "redirect:/admin/students";
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
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/teachers/form";
        }
        try {
            teacherService.updateTeacher(id, teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully");
            return "redirect:/admin/teachers";
        } catch (RuntimeException ex) {
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

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses/list";
    }

    @GetMapping("/courses/add")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "admin/courses/form";
    }

    @PostMapping("/courses/add")
    public String addCourse(@Valid @ModelAttribute("course") CourseDTO course,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherService.getAllTeachers());
            return "admin/courses/form";
        }
        try {
            courseService.createCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully");
            return "redirect:/admin/courses";
        } catch (RuntimeException ex) {
            result.rejectValue("code", "error.course", ex.getMessage());
            model.addAttribute("teachers", teacherService.getAllTeachers());
            return "admin/courses/form";
        }
    }

    @GetMapping("/courses/{id}/edit")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "admin/courses/form";
    }

    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                           @Valid @ModelAttribute("course") CourseDTO course,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherService.getAllTeachers());
            return "admin/courses/form";
        }
        try {
            courseService.updateCourse(id, course);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully");
            return "redirect:/admin/courses";
        } catch (RuntimeException ex) {
            result.rejectValue("code", "error.course", ex.getMessage());
            model.addAttribute("teachers", teacherService.getAllTeachers());
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
}
