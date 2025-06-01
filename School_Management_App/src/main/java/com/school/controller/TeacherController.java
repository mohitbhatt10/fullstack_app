package com.school.controller;

import com.school.dto.AttendanceDTO;
import com.school.dto.AttendanceSummaryDTO;
import com.school.dto.MarkDTO;
import com.school.dto.StudentDTO;
import com.school.service.*;
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
    private final CourseScheduleService courseScheduleService;

    public TeacherController(CourseService courseService,
                           StudentService studentService,
                           MarkService markService,
                           AttendanceService attendanceService,
                           TeacherService teacherService, CourseScheduleService courseScheduleService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.markService = markService;
        this.attendanceService = attendanceService;
        this.teacherService = teacherService;
        this.courseScheduleService = courseScheduleService;
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
        model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
        
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
        
            // Get attendance summary records for this course
            List<AttendanceSummaryDTO> attendanceSummaryList = attendanceService.getAttendanceSummaryByCourseId(courseId);
            
            // Get attendance records for this course
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
            model.addAttribute("attendanceSummaryList", attendanceSummaryList);
            model.addAttribute("students", students);
            
            return "teacher/attendance";
        }
    
        @GetMapping("/courses/{courseId}/attendance/{scheduleId}/{date}/edit")
        public String editAttendance(@PathVariable Long courseId, 
                                    @PathVariable Long scheduleId,
                                    @PathVariable String date,
                                    Model model, 
                                    Principal principal) {
            
            // Get course details
            model.addAttribute("course", courseService.getCourseById(courseId));
            
            // Parse the date
            LocalDate attendanceDate = LocalDate.parse(date);
            model.addAttribute("attendanceDate", attendanceDate);
            model.addAttribute("scheduleId", scheduleId);
            
            // Get schedule info
            String scheduleInfo = "";
            List<AttendanceDTO> attendanceRecords = attendanceService.getAttendanceByCourseScheduleAndDate(courseId, scheduleId, attendanceDate);
            if (!attendanceRecords.isEmpty()) {
                scheduleInfo = attendanceRecords.get(0).getScheduleInfo();
            }
            model.addAttribute("scheduleInfo", scheduleInfo);
            
            // Get attendance records for this course, schedule and date
            model.addAttribute("attendanceRecords", attendanceRecords);
            
            return "teacher/edit-attendance";
        }
        
        @PostMapping("/attendance/update")
        public String updateAttendance(@RequestParam Long courseId,
                                     @RequestParam Long scheduleId,
                                     @RequestParam String date,
                                     @RequestParam Map<String, String> allParams,
                                     Principal principal,
                                     Model model) {
            
            LocalDate attendanceDate = LocalDate.parse(date);
            List<AttendanceDTO> attendanceUpdates = new ArrayList<>();

            // Get the teacher ID from the authenticated user
            String username = principal.getName();
            Long teacherId = teacherService.getTeacherByUsername(username).getId();
            // Process attendance entries using the array structure
            for (String key : allParams.keySet()) {
                if (key.matches("attendances\\[\\d+\\]\\.id")) {
                    // Extract the index from the parameter name
                    String indexStr = key.substring("attendances[".length(), key.indexOf("]"));
                    int index = Integer.parseInt(indexStr);
                    
                    // Get the attendance ID
                    Long attendanceId = Long.parseLong(allParams.get(key));
                    
                    // Get the student ID
                    String studentIdKey = "attendances[" + index + "].studentId";
                    Long studentId = Long.parseLong(allParams.get(studentIdKey));
                    
                    // Check if the corresponding "present" parameter exists
                    String presentKey = "attendances[" + index + "].present";
                    boolean isPresent = allParams.containsKey(presentKey);
                    
                    // Get remarks if available
                    String remarksKey = "attendances[" + index + "].comments";
                    String comments = allParams.getOrDefault(remarksKey, "");
                    
                    // Create the attendance update object
                    AttendanceDTO attendance = new AttendanceDTO();
                    attendance.setId(attendanceId);
                    attendance.setStudentId(studentId);
                    attendance.setPresent(isPresent);
                    attendance.setComments(comments);
                    attendance.setScheduleId(scheduleId);
                    attendance.setCourseId(courseId);
                    attendance.setMarkedByTeacherId(teacherId);
                    attendance.setDate(attendanceDate);

                    attendanceUpdates.add(attendance);
                }
            }
            
            // Update the attendance records
            attendanceService.updateAttendance(attendanceUpdates);
            return "redirect:/teacher/courses/" + courseId + "/attendance";
    }

    @PostMapping("/courses/{courseId}/attendance")
        public String markAttendance(@PathVariable Long courseId,
                                   @Valid @ModelAttribute("attendance") AttendanceDTO attendance,
                                   BindingResult result,
                                   Model model) {
            if (result.hasErrors()) {
                model.addAttribute("course", courseService.getCourseById(courseId));
                model.addAttribute("students", studentService.getStudentsByCourseId(courseId));
                model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
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
        Long scheduleId = Long.parseLong(allParams.get("scheduleId"));

        // Process attendance entries using the array structure
        // Attendance records come in as attendances[0].studentId, attendances[0].present, etc.
        Map<Long, Boolean> studentAttendances = new HashMap<>();
        Map<Long, String> studentComments = new HashMap<>();
        
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
                String comments = "attendances[" + index + "].comments";
                if (allParams.containsKey(comments)) {
                    studentComments.put(studentId, allParams.get(comments));
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
            
            // Add comments if available
            if (studentComments.containsKey(studentId)) {
                // If your AttendanceDTO has a comment field, set it here
                attendance.setComments(studentComments.get(studentId));
            }
            attendance.setScheduleId(scheduleId);
            attendanceService.createAttendance(attendance);
        }
        
        return "redirect:/teacher/courses/" + courseIdLong + "/attendance";
    }

    @GetMapping("/courses/{courseId}/marks")
    public String showMarksForm(@PathVariable Long courseId, 
                               @RequestParam(required = false) String examType,
                               Model model) {
        // Get course details
        model.addAttribute("course", courseService.getCourseById(courseId));

        // Get students for this course
        List<StudentDTO> students = studentService.getStudentsByCourseId(courseId);

        // If examType is provided, try to load existing marks for these students
        if (examType != null && !examType.isEmpty()) {
            try {
                // Get existing marks for this course and exam type
                Map<Long, Double> studentMarks = new HashMap<>();
                List<MarkDTO> marks = markService.getMarksByCourseId(courseId);

                for (MarkDTO mark : marks) {
                    if (mark.getExamType().equals(examType)) {
                        studentMarks.put(mark.getStudentId(), mark.getMarks());
                    }
                }

                // Set marks for each student if they exist
                for (StudentDTO student : students) {
                    if (studentMarks.containsKey(student.getId())) {
                        student.setMarks(studentMarks.get(student.getId()));
                    }
                }

                // Add examType to model for the form selection
                model.addAttribute("selectedExamType", examType);
            } catch (Exception e) {
                logger.error("Error loading existing marks", e);
                // Continue without marks if there's an error
            }
        }

        model.addAttribute("students", students);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("mark", new MarkDTO());

        return "teacher/marks-form";
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

    @PostMapping("/marks/submit")
    public String submitMarks(@RequestParam String courseId,
                            @RequestParam String examType,
                            @RequestParam String maxMarks,
                            @RequestParam Map<String, String> allParams,
                            Principal principal,
                            Model model) {

        // Log the received parameters for debugging
        logger.info("Received marks submission - courseId: {}, examType: {}, maxMarks: {}", courseId, examType, maxMarks);

        // Parse parameters
        Long courseIdLong = Long.parseLong(courseId);
        Double maxMarksValue = Double.parseDouble(maxMarks);

        // Get the teacher ID from the authenticated user
        String username = principal.getName();
        Long teacherId = teacherService.getTeacherByUsername(username).getId();

        if (teacherId == null) {
            logger.error("Could not find teacher with username: {}", username);
            throw new RuntimeException("Teacher not found. Please log in with a valid teacher account.");
        }

        // Process marks entries using the array structure
        for (String key : allParams.keySet()) {
            if (key.matches("marks\\[\\d+\\]\\.studentId")) {
                // Extract the index from the parameter name
                String indexStr = key.substring("marks[".length(), key.indexOf("]"));
                int index = Integer.parseInt(indexStr);

                // Get the studentId value
                Long studentId = Long.parseLong(allParams.get(key));

                // Get marks value if available
                String marksKey = "marks[" + index + "].marks";
                if (allParams.containsKey(marksKey)) {
                    Double marksValue = Double.parseDouble(allParams.get(marksKey));

                    // Get remarks if available
                    String remarksKey = "marks[" + index + "].remarks";
                    String remarks = allParams.getOrDefault(remarksKey, "");

                    // Create the mark object
                    MarkDTO mark = new MarkDTO();
                    mark.setStudentId(studentId);
                    mark.setCourseId(courseIdLong);
                    mark.setExamType(examType);
                    mark.setMarks(marksValue);
                    mark.setMaxMarks(maxMarksValue);

                    // Get student's semester from the student service
                    StudentDTO student = studentService.getStudentById(studentId);
                    if (student != null) {
                        mark.setSemester(student.getSemester());
                    } else {
                        logger.warn("Student with ID {} not found when submitting marks", studentId);
                        // Default to semester 1 if student not found
                        mark.setSemester(1);
                    }

                    // Set the teacher ID who entered these marks
                    mark.setEnteredByTeacherId(teacherId);

                    // Save the mark
                    try {
                        markService.createMark(mark);
                        logger.info("Saved mark for student {} in course {}: {}", studentId, courseIdLong, marksValue);
                    } catch (Exception e) {
                        logger.error("Error saving mark for student {}: {}", studentId, e.getMessage());
                    }
                }
            }
        }

        return "redirect:/teacher/courses/" + courseIdLong + "/marks";
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