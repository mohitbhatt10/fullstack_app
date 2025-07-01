package com.school.controller;

import com.school.dto.StudentDTO;
import com.school.service.AttendanceService;
import com.school.service.CourseScheduleService;
import com.school.service.CourseService;
import com.school.service.MarkService;
import com.school.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final MarkService markService;
    private final AttendanceService attendanceService;
    private final CourseScheduleService courseScheduleService;

    public StudentController(StudentService studentService,
                           CourseService courseService,
                           MarkService markService,
                           AttendanceService attendanceService,
                           CourseScheduleService courseScheduleService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.markService = markService;
        this.attendanceService = attendanceService;
        this.courseScheduleService = courseScheduleService;
    }

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        Long studentId = studentService.getStudentByUsername(principal.getName()).getId();
        model.addAttribute("courses", courseService.getCoursesByStudentId(studentId));
        
        // Get recent attendance (last 5 records)
        model.addAttribute("recentAttendance", attendanceService.getRecentAttendanceForStudent(studentId, 5));
        
        // Get recent marks (last 5 records)
        model.addAttribute("recentMarks", markService.getRecentMarksForStudent(studentId, 5));
        
        return "student/dashboard";
    }

    @GetMapping("/marks")
    public String viewMarks(Model model, Principal principal) {
        Long studentId = studentService.getStudentByUsername(principal.getName()).getId();
        model.addAttribute("courses", courseService.getCoursesByStudentId(studentId));
        model.addAttribute("marks", markService.getMarksByStudentId(studentId));
        return "student/marks";
    }

    @GetMapping("/marks/{courseId}")
    public String viewCourseMarks(@PathVariable Long courseId,
                                Model model, Principal principal) {
        Long studentId = studentService.getStudentByUsername(principal.getName()).getId();
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("marks", markService.getMarksByStudentIdAndCourseId(studentId, courseId));
        return "student/course-marks";
    }

    @GetMapping("/attendance")
    public String viewAttendance(Model model, Principal principal) {
        Long studentId = studentService.getStudentByUsername(principal.getName()).getId();
        model.addAttribute("courses", courseService.getCoursesByStudentId(studentId));
        model.addAttribute("attendance", attendanceService.getAttendanceByStudentId(studentId));
        return "student/attendance";
    }

    @GetMapping("/attendance/{courseId}")
    public String viewCourseAttendance(@PathVariable Long courseId,
                                     Model model, Principal principal) {
        Long studentId = studentService.getStudentByUsername(principal.getName()).getId();
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("attendance", 
            attendanceService.getAttendanceByStudentIdAndCourseId(studentId, courseId));
        return "student/course-attendance";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute @Valid StudentDTO studentDTO,
                              BindingResult result,
                              @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "student/profile";
        }

        try {
            if (profilePicture != null && !profilePicture.isEmpty()) {
                studentDTO.setProfilePicture(profilePicture.getBytes());
                studentDTO.setProfilePictureContentType(profilePicture.getContentType());
            }

            studentService.updateStudent(studentDTO.getId(), studentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/student/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            return "redirect:/student/profile";
        }
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        StudentDTO student = studentService.getStudentByUsername(username);
        model.addAttribute("student", student);
        return "student/profile";
    }

    @GetMapping("/courses/{courseId}")
    public String viewCourseDetails(@PathVariable Long courseId,
                                  Model model, Principal principal) {
        Long studentId = studentService.getStudentByUsername(principal.getName()).getId();
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("marks", markService.getMarksByStudentIdAndCourseId(studentId, courseId));
        model.addAttribute("attendance", attendanceService.getAttendanceByStudentIdAndCourseId(studentId, courseId));
        model.addAttribute("attendancePercentage", attendanceService.getAttendancePercentage(studentId, courseId));
        model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
        return "student/course-details";
    }
}
