package com.school.controller;

import com.school.dto.AttendanceDTO;
import com.school.dto.MarkDTO;
import com.school.service.AttendanceService;
import com.school.service.CourseService;
import com.school.service.MarkService;
import com.school.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final MarkService markService;
    private final AttendanceService attendanceService;

    public TeacherController(CourseService courseService,
                           StudentService studentService,
                           MarkService markService,
                           AttendanceService attendanceService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.markService = markService;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Principal principal) {
        model.addAttribute("courses", courseService.getCoursesByTeacherUsername(principal.getName()));
        return "teacher/dashboard";
    }

    @GetMapping("/courses/{courseId}/students")
    public String listStudentsInCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("students", studentService.getStudentsByCourseId(courseId));
        return "teacher/course/students";
    }

    @GetMapping("/courses/{courseId}/attendance")
    public String showAttendanceForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("students", studentService.getStudentsByCourseId(courseId));
        model.addAttribute("attendance", new AttendanceDTO());
        
        // Add attendance window information
        LocalDate today = LocalDate.now();
        Map<String, LocalTime> timeWindow = attendanceService.getAttendanceTimeWindow(courseId, today);
        if (timeWindow != null) {
            model.addAttribute("timeWindow", timeWindow);
        }
        
        return "teacher/course/attendance-form";
    }

    @PostMapping("/courses/{courseId}/attendance")
    public String markAttendance(@PathVariable Long courseId,
                               @Valid @ModelAttribute("attendance") AttendanceDTO attendance,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/course/attendance-form";
        }
        attendanceService.createAttendance(attendance);
        return "redirect:/teacher/courses/" + courseId + "/attendance";
    }

    @GetMapping("/courses/{courseId}/marks")
    public String showMarksForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("students", studentService.getStudentsByCourseId(courseId));
        model.addAttribute("mark", new MarkDTO());
        return "teacher/course/marks-form";
    }

    @PostMapping("/courses/{courseId}/marks")
    public String enterMarks(@PathVariable Long courseId,
                           @Valid @ModelAttribute("mark") MarkDTO mark,
                           BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/course/marks-form";
        }
        markService.createMark(mark);
        return "redirect:/teacher/courses/" + courseId + "/marks";
    }

    @GetMapping("/courses/{courseId}/marks/{studentId}")
    public String viewStudentMarks(@PathVariable Long courseId,
                                 @PathVariable Long studentId,
                                 Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("marks", markService.getMarksByStudentIdAndCourseId(studentId, courseId));
        return "teacher/course/student-marks";
    }
}
