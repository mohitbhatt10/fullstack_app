package com.school.controller;

import com.school.dto.CourseDTO;
import com.school.dto.CourseEnrollmentDTO;
import com.school.dto.SessionDTO;
import com.school.dto.StudentDTO;
import com.school.service.CourseEnrollmentService;
import com.school.service.CourseService;
import com.school.service.SessionService;
import com.school.service.StudentService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/enrollments")
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final SessionService sessionService;

    public CourseEnrollmentController(CourseEnrollmentService enrollmentService,
                                     StudentService studentService,
                                     CourseService courseService,
                                     SessionService sessionService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public String listEnrollments(Model model) {
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
        return "admin/enrollments/list";
    }

    @GetMapping("/new")
    public String newEnrollmentForm(Model model) {
        model.addAttribute("enrollment", new CourseEnrollmentDTO());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("sessions", sessionService.getAllSessions());
        return "admin/enrollments/form";
    }

    @PostMapping
    public String createEnrollment(@Valid @ModelAttribute("enrollment") CourseEnrollmentDTO enrollmentDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("sessions", sessionService.getAllSessions());
            return "admin/enrollments/form";
        }

        enrollmentService.createEnrollment(enrollmentDTO);
        return "redirect:/admin/enrollments";
    }

    @GetMapping("/{id}/edit")
    public String editEnrollmentForm(@PathVariable Long id, Model model) {
        model.addAttribute("enrollment", enrollmentService.getEnrollmentById(id));
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("sessions", sessionService.getAllSessions());
        return "admin/enrollments/form";
    }

    @PostMapping("/{id}")
    public String updateEnrollment(@PathVariable Long id,
                                 @Valid @ModelAttribute("enrollment") CourseEnrollmentDTO enrollmentDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("sessions", sessionService.getAllSessions());
            return "admin/enrollments/form";
        }

        enrollmentService.updateEnrollment(id, enrollmentDTO);
        return "redirect:/admin/enrollments";
    }

    @PostMapping("/{id}/withdraw")
    public String withdrawStudent(@PathVariable Long id) {
        CourseEnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        enrollmentService.withdrawStudentFromCourse(enrollment.getStudentId(), enrollment.getCourseId());
        return "redirect:/admin/enrollments";
    }

    @GetMapping("/student/{studentId}")
    public String listStudentEnrollments(@PathVariable Long studentId, Model model) {
        StudentDTO student = studentService.getStudentById(studentId);
        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollmentService.getEnrollmentsByStudentId(studentId));
        return "admin/enrollments/student";
    }

    @GetMapping("/course/{courseId}")
    public String listCourseEnrollments(@PathVariable Long courseId, Model model) {
        CourseDTO course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollmentService.getEnrollmentsByCourseId(courseId));
        return "admin/enrollments/course";
    }

    @GetMapping("/session/{sessionId}")
    public String listSessionEnrollments(@PathVariable Long sessionId, Model model) {
        SessionDTO session = sessionService.getSessionById(sessionId);
        model.addAttribute("academicSession", session);
        model.addAttribute("enrollments", enrollmentService.getEnrollmentsBySessionId(sessionId));
        return "admin/enrollments/session";
    }
}
