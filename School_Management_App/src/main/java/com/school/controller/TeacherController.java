package com.school.controller;

import com.school.dto.AttendanceDTO;
import com.school.dto.MarkDTO;
import com.school.dto.StudentDTO;
import com.school.service.AttendanceService;
import com.school.service.CourseService;
import com.school.service.MarkService;
import com.school.service.StudentService;
import com.school.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TeacherController.class);

    private final CourseService courseService;
    private final StudentService studentService;
    private final MarkService markService;
    private final AttendanceService attendanceService;
    private final TeacherService teacherService;

    public TeacherController(CourseService courseService,
                           StudentService studentService,
                           MarkService markService,
                           AttendanceService attendanceService,
                           TeacherService teacherService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.markService = markService;
        this.attendanceService = attendanceService;
        this.teacherService = teacherService;
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

    @GetMapping("/courses/{courseId}/attendance/form")
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
        
        return "teacher/attendance-form";
    }
    
    @GetMapping("/courses/{courseId}/attendance")
    public String viewCourseAttendance(@PathVariable Long courseId, Model model, Principal principal) {
        // Get the current authenticated teacher's username
        String username = principal.getName();
        
        // Get course details
        model.addAttribute("course", courseService.getCourseById(courseId));
        
        // Get attendance records for this course (if you have this method)
        // If not available, use an empty list or implement in AttendanceService
        List<AttendanceDTO> attendanceRecords = new ArrayList<>();
        try {
            attendanceRecords = attendanceService.getAttendanceByCourseId(courseId);
        } catch (Exception e) {
            // Handle case when method doesn't exist or errors
            attendanceRecords = new ArrayList<>();
        }
        
        // Get students enrolled in the course
        List<StudentDTO> students = studentService.getStudentsByCourseId(courseId);
        
        model.addAttribute("attendanceRecords", attendanceRecords);
        model.addAttribute("students", students);
        
        return "teacher/attendance";
    }
    
    @PostMapping("/courses/{courseId}/attendance")
    public String markAttendance(@PathVariable Long courseId,
                               @Valid @ModelAttribute("attendance") AttendanceDTO attendance,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/attendance-form";
        }
        attendanceService.createAttendance(attendance);
        return "redirect:/teacher/courses/" + courseId + "/attendance";
    }
    
    @PostMapping("/attendance/submit")
    public String submitAttendance(@RequestParam String courseId, 
                                  @RequestParam String date,
                                  @RequestParam Map<String, String> allParams, 
                                  Principal principal,
                                  Model model) {
        
        // Log the received parameters for debugging
        logger.info("Received courseId: {}, date: {}", courseId, date);
        
        // Parse courseId
        Long courseIdLong = Long.parseLong(courseId);
        LocalDate attendanceDate = LocalDate.parse(date);

        // Process attendance entries using the array structure
        // Attendance records come in as attendances[0].studentId, attendances[0].present, etc.
        Map<Long, Boolean> studentAttendances = new HashMap<>();
        Map<Long, String> studentRemarks = new HashMap<>();
        
        for (String key : allParams.keySet()) {
            if (key.matches("attendances\\[\\d+\\]\\.studentId")) {
                // Extract the index from the parameter name
                String indexStr = key.substring("attendances[".length(), key.indexOf("]"));
                int index = Integer.parseInt(indexStr);
                
                // Get the studentId value
                Long studentId = Long.parseLong(allParams.get(key));
                
                // Check if the corresponding "present" parameter exists
                String presentKey = "attendances[" + index + "].present";
                boolean isPresent = allParams.containsKey(presentKey);
                
                // Store the values
                studentAttendances.put(studentId, isPresent);
                
                // Get remarks if available
                String remarksKey = "attendances[" + index + "].remarks";
                if (allParams.containsKey(remarksKey)) {
                    studentRemarks.put(studentId, allParams.get(remarksKey));
                }
            }
        }
        
        // Get the teacher ID from the authenticated user
        String username = principal.getName();
        Long teacherId = teacherService.getTeacherByUsername(username).getId();
        
        if (teacherId == null) {
            logger.error("Could not find teacher with username: {}", username);
            throw new RuntimeException("Teacher not found. Please log in with a valid teacher account.");
        }
        
        logger.info("Teacher ID {} is marking attendance for course {}", teacherId, courseIdLong);
        
        // Now process all the attendance records
        for (Map.Entry<Long, Boolean> entry : studentAttendances.entrySet()) {
            Long studentId = entry.getKey();
            Boolean isPresent = entry.getValue();
        
            AttendanceDTO attendance = new AttendanceDTO();
            attendance.setStudentId(studentId);
            attendance.setCourseId(courseIdLong);
            attendance.setDate(attendanceDate);
            attendance.setPresent(isPresent);
        
            // Set teacher who marked attendance
            attendance.setMarkedByTeacherId(teacherId);
            
            // Add remarks if available
            if (studentRemarks.containsKey(studentId)) {
                // If your AttendanceDTO has a remarks field, set it here
                // attendance.setRemarks(studentRemarks.get(studentId));
            }

            attendanceService.createAttendance(attendance);
        }
        
        return "redirect:/teacher/courses/" + courseIdLong + "/attendance";
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