package com.school.controller;

import com.school.dto.*;
import com.school.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ExamTypeService examTypeService;

    public TeacherController(CourseService courseService,
                           StudentService studentService,
                           MarkService markService,
                           AttendanceService attendanceService,
                           TeacherService teacherService, 
                           CourseScheduleService courseScheduleService,
                           ExamTypeService examTypeService
                           ) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.markService = markService;
        this.attendanceService = attendanceService;
        this.teacherService = teacherService;
        this.courseScheduleService = courseScheduleService;
        this.examTypeService = examTypeService;
    }

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Principal principal) {
        String username = principal.getName();
        List<CourseDTO> courses = courseService.getCoursesByTeacherUsername(username);
        model.addAttribute("courses", courses);

        // Get recent attendance summaries (last 3)
        List<AttendanceSummaryDTO> recentAttendance = attendanceService.getAttendanceSummaryByTeacher(username)
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        // Get recent marks (last 3)
        List<MarksSummaryDTO> recentMarks = markService.getMarksSummaryByTeacher(username)
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        model.addAttribute("courses", courses);
        model.addAttribute("recentAttendance", recentAttendance);
        model.addAttribute("recentMarks", recentMarks);
        
        return "teacher/dashboard";
    }

    @GetMapping("/courses/{courseId}/students")
    public String viewCourseStudents(@PathVariable Long courseId, Model model) {
        CourseDTO course = courseService.getCourseById(courseId);
        List<StudentDTO> students = studentService.getStudentsByCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("students", students);
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
            List<AttendanceDTO> existingAttendanceRecords = attendanceService.getAttendanceByCourseScheduleAndDate(courseId, scheduleId, attendanceDate);
            if (!existingAttendanceRecords.isEmpty()) {
                scheduleInfo = existingAttendanceRecords.get(0).getScheduleInfo();
            }
            model.addAttribute("scheduleInfo", scheduleInfo);

            // Get all current students enrolled in the course
            List<StudentDTO> currentStudents = studentService.getStudentsByCourseId(courseId);

            // Create a map of existing attendance records by student ID
            Map<Long, AttendanceDTO> attendanceByStudentId = new HashMap<>();
            for (AttendanceDTO record : existingAttendanceRecords) {
                attendanceByStudentId.put(record.getStudentId(), record);
            }

            // Get the teacher ID from the authenticated user
            String username = principal.getName();
            Long teacherId = teacherService.getTeacherByUsername(username).getId();

            // Create a combined list of attendance records that includes all current students
            List<AttendanceDTO> allAttendanceRecords = new ArrayList<>();

            // Add all existing records first
            allAttendanceRecords.addAll(existingAttendanceRecords);

            // Add newly enrolled students who don't have attendance records yet
            for (StudentDTO student : currentStudents) {
                if (!attendanceByStudentId.containsKey(student.getId())) {
                    // Create a new attendance record for this student (marked as absent by default)
                    AttendanceDTO newRecord = new AttendanceDTO();
                    newRecord.setStudentId(student.getId());
                    newRecord.setStudentName(student.getFirstName() + " " + student.getLastName());
                    newRecord.setCourseId(courseId);
                    newRecord.setScheduleId(scheduleId);
                    newRecord.setDate(attendanceDate);
                    newRecord.setPresent(false); // Default to absent
                    newRecord.setComments("");
                    newRecord.setMarkedByTeacherId(teacherId);
                    newRecord.setId(null); // This is a new record, so no ID yet

                    // Add to the list of all records
                    allAttendanceRecords.add(newRecord);
                }
            }

            // Sort the records by student name for consistency
            allAttendanceRecords.sort(Comparator.comparing(AttendanceDTO::getStudentName));

            // Add to the model
            model.addAttribute("attendanceRecords", allAttendanceRecords);
            
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
            List<AttendanceDTO> newAttendanceRecords = new ArrayList<>();

            // Get the teacher ID from the authenticated user
            String username = principal.getName();
            Long teacherId = teacherService.getTeacherByUsername(username).getId();

            // Process attendance entries using the array structure
            for (String key : allParams.keySet()) {
                if (key.matches("attendances\\[\\d+\\]\\.studentId")) {
                    // Extract the index from the parameter name
                    String indexStr = key.substring("attendances[".length(), key.indexOf("]"));
                    int index = Integer.parseInt(indexStr);

                    // Get the student ID
                    Long studentId = Long.parseLong(allParams.get(key));

                    // Check if the corresponding "present" parameter exists
                    String presentKey = "attendances[" + index + "].present";
                    boolean isPresent = allParams.containsKey(presentKey);

                    // Get remarks if available
                    String remarksKey = "attendances[" + index + "].comments";
                    String comments = allParams.getOrDefault(remarksKey, "");

                    // Check if there's an ID field (existing record) or not (new record)
                    String idKey = "attendances[" + index + "].id";

                    if (allParams.containsKey(idKey) && !allParams.get(idKey).isEmpty()) {
                        // This is an existing record to update
                        Long attendanceId = Long.parseLong(allParams.get(idKey));

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
                    } else {
                        // This is a new record to create
                        AttendanceDTO attendance = new AttendanceDTO();
                        attendance.setStudentId(studentId);
                        attendance.setPresent(isPresent);
                        attendance.setComments(comments);
                        attendance.setScheduleId(scheduleId);
                        attendance.setCourseId(courseId);
                        attendance.setMarkedByTeacherId(teacherId);
                        attendance.setDate(attendanceDate);

                        newAttendanceRecords.add(attendance);
                    }
                }
            }

            // Update existing attendance records
            if (!attendanceUpdates.isEmpty()) {
                attendanceService.updateAttendance(attendanceUpdates);
            }

            // Create new attendance records
            for (AttendanceDTO newRecord : newAttendanceRecords) {
                attendanceService.createAttendance(newRecord);
            }

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

    @GetMapping("/courses/{courseId}/marks/form")
    public String showMarksForm(@PathVariable Long courseId, 
                               @RequestParam(required = false) Long examTypeId,
                               Model model) {
        // Get course details
        model.addAttribute("course", courseService.getCourseById(courseId));

        // Get active exam types for the dropdown
        model.addAttribute("examTypes", examTypeService.getActiveExamTypesOrdered());

        // Get students for this course
        List<StudentDTO> students = studentService.getStudentsByCourseId(courseId);

        // If examTypeId is provided, try to load existing marks for these students
        if (examTypeId != null) {
            try {
                // Get existing marks for this course and exam type
                Map<Long, Double> studentMarks = new HashMap<>();
                List<MarkDTO> marks = markService.getMarksByCourseId(courseId);

                for (MarkDTO mark : marks) {
                    if (mark.getExamTypeId().equals(examTypeId)) {
                        studentMarks.put(mark.getStudentId(), mark.getMarks());
                    }
                }

                // Set marks for each student if they exist
                for (StudentDTO student : students) {
                    if (studentMarks.containsKey(student.getId())) {
                        student.setMarks(studentMarks.get(student.getId()));
                    }
                }

                // Add examTypeId to model for the form selection
                model.addAttribute("selectedExamTypeId", examTypeId);
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

    @GetMapping("/courses/{courseId}/marks")
    public String viewCourseMarks(@PathVariable Long courseId, Model model, Principal principal) {
        // Get the current authenticated teacher's username
        String username = principal.getName();

        // Get course details
        model.addAttribute("course", courseService.getCourseById(courseId));

        // Get all marks for this course
        List<MarkDTO> marksList = markService.getMarksByCourseId(courseId);

        // Group marks by exam type and calculate average for each type
        Map<String, Map<String, Object>> marksByExamType = new HashMap<>();

        for (MarkDTO mark : marksList) {
            String examTypeName = mark.getExamTypeName();
            if (examTypeName != null && !marksByExamType.containsKey(examTypeName)) {
                Map<String, Object> examStats = new HashMap<>();
                examStats.put("count", 0);
                examStats.put("totalMarks", 0.0);
                examStats.put("maxMarks", mark.getMaxMarks());
                examStats.put("averageMarks", 0.0);
                examStats.put("examTypeId", mark.getExamTypeId());
                marksByExamType.put(examTypeName, examStats);
            }

            if (examTypeName != null) {
                Map<String, Object> examStats = marksByExamType.get(examTypeName);
                int count = (int) examStats.get("count") + 1;
                double totalMarks = (double) examStats.get("totalMarks") + mark.getMarks();
                double averageMarks = totalMarks / count;

                examStats.put("count", count);
                examStats.put("totalMarks", totalMarks);
                examStats.put("averageMarks", averageMarks);
            }
        }

        // Get students enrolled in the course
        List<StudentDTO> students = studentService.getStudentsByCourseId(courseId);

        model.addAttribute("marksByExamType", marksByExamType);
        model.addAttribute("students", students);

        return "teacher/marks";
    }

    @GetMapping("/courses/{courseId}/marks/{examTypeId}/edit")
    public String editMarks(@PathVariable Long courseId,
                           @PathVariable Long examTypeId,
                           Model model,
                           Principal principal) {
        // Get course details
        model.addAttribute("course", courseService.getCourseById(courseId));
        
        // Get exam type details
        try {
            model.addAttribute("examType", examTypeService.getExamTypeById(examTypeId));
        } catch (Exception e) {
            logger.error("Error getting exam type with ID {}: {}", examTypeId, e.getMessage());
            return "redirect:/teacher/courses/" + courseId + "/marks";
        }

        // Get all marks for this course and exam type
        List<MarkDTO> marksList = markService.getMarksByCourseId(courseId);
        List<MarkDTO> existingMarksForExamType = new ArrayList<>();
        Double maxMarks = 0.0;

        // Filter marks by exam type ID
        for (MarkDTO mark : marksList) {
            if (mark.getExamTypeId() != null && mark.getExamTypeId().equals(examTypeId)) {
                existingMarksForExamType.add(mark);
                // Get max marks (should be the same for all marks of this exam type)
                maxMarks = mark.getMaxMarks();
            }
        }

        // Get all current students enrolled in the course
        List<StudentDTO> currentStudents = studentService.getStudentsByCourseId(courseId);

        // Create a map of existing marks records by student ID
        Map<Long, MarkDTO> marksByStudentId = new HashMap<>();
        for (MarkDTO mark : existingMarksForExamType) {
            marksByStudentId.put(mark.getStudentId(), mark);
        }

        // Get the teacher ID from the authenticated user
        String username = principal.getName();
        Long teacherId = teacherService.getTeacherByUsername(username).getId();

        // Create a combined list of mark records that includes all current students
        List<MarkDTO> allMarksRecords = new ArrayList<>();

        // Add all existing records first
        allMarksRecords.addAll(existingMarksForExamType);

        // Add newly enrolled students who don't have marks records yet
        for (StudentDTO student : currentStudents) {
            if (!marksByStudentId.containsKey(student.getId())) {
                // Create a new mark record for this student (marked as 0 by default)
                MarkDTO newRecord = new MarkDTO();
                newRecord.setStudentId(student.getId());
                newRecord.setStudentName(student.getFirstName() + " " + student.getLastName());
                newRecord.setCourseId(courseId);
                newRecord.setExamTypeId(examTypeId);
                newRecord.setMarks(0.0); // Default to 0
                newRecord.setMaxMarks(maxMarks);
                newRecord.setSemester(student.getSemester());
                newRecord.setEnteredByTeacherId(teacherId);
                newRecord.setId(null); // This is a new record, so no ID yet

                // Add to the list of all records
                allMarksRecords.add(newRecord);
            }
        }

        // Sort the records by student name for consistency
        allMarksRecords.sort(Comparator.comparing(mark -> {
            // If studentName is null or empty, try to get student details
            if (mark.getStudentName() == null || mark.getStudentName().isEmpty()) {
                try {
                    StudentDTO student = studentService.getStudentById(mark.getStudentId());
                    return student.getFirstName() + " " + student.getLastName();
                } catch (Exception e) {
                    return "Unknown Student";
                }
            }
            return mark.getStudentName();
        }));

        model.addAttribute("marksRecords", allMarksRecords);
        model.addAttribute("maxMarks", maxMarks);

        return "teacher/edit-marks";
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
        Long examTypeId = Long.parseLong(examType); // Now expecting exam type ID
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
                    mark.setExamTypeId(examTypeId);
                    mark.setMarks(marksValue);
                    mark.setMaxMarks(maxMarksValue);
                    mark.setRemarks(remarks);

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

    @PostMapping("/marks/update")
    public String updateMarks(@RequestParam String courseId,
                            @RequestParam String examType,
                            @RequestParam String maxMarks,
                            @RequestParam Map<String, String> allParams,
                            Principal principal,
                            Model model) {

        // Parse parameters
        Long courseIdLong = Long.parseLong(courseId);
        Long examTypeId = Long.parseLong(examType); // Now expecting exam type ID
        Double maxMarksValue = Double.parseDouble(maxMarks);

        // Get the teacher ID from the authenticated user
        String username = principal.getName();
        Long teacherId = teacherService.getTeacherByUsername(username).getId();

        if (teacherId == null) {
            logger.error("Could not find teacher with username: {}", username);
            throw new RuntimeException("Teacher not found. Please log in with a valid teacher account.");
        }

        // Lists to track existing marks to update and new marks to create
        List<MarkDTO> existingMarksToUpdate = new ArrayList<>();
        List<MarkDTO> newMarksToCreate = new ArrayList<>();

        // Process marks entries using the array structure
        for (String key : allParams.keySet()) {
            if (key.matches("marks\\[\\d+\\]\\.studentId")) {
                // Extract the index from the parameter name
                String indexStr = key.substring("marks[".length(), key.indexOf("]"));
                int index = Integer.parseInt(indexStr);

                // Get the student ID
                Long studentId = Long.parseLong(allParams.get(key));

                // Get marks value
                String marksKey = "marks[" + index + "].marks";
                Double marksValue = 0.0;
                if (allParams.containsKey(marksKey)) {
                    marksValue = Double.parseDouble(allParams.get(marksKey));
                }

                // Get remarks if available
                String remarksKey = "marks[" + index + "].remarks";
                String remarks = allParams.getOrDefault(remarksKey, "");

                // Check if there's an ID field (existing record) or not (new record)
                String idKey = "marks[" + index + "].id";

                if (allParams.containsKey(idKey) && !allParams.get(idKey).isEmpty()) {
                    // This is an existing record to update
                    Long markId = Long.parseLong(allParams.get(idKey));

                    try {
                        // Get the existing mark and update its value
                        MarkDTO mark = markService.getMarkById(markId);
                        mark.setMarks(marksValue);
                        mark.setRemarks(remarks);

                        existingMarksToUpdate.add(mark);

                        logger.info("Prepared update for mark ID {} for student {} in course {}: {}", 
                                   markId, studentId, courseIdLong, marksValue);
                    } catch (Exception e) {
                        logger.error("Error preparing update for mark ID {} for student {}: {}", 
                                    markId, studentId, e.getMessage());
                    }
                } else {
                    // This is a new record to create
                    MarkDTO newMark = new MarkDTO();
                    newMark.setStudentId(studentId);
                    newMark.setCourseId(courseIdLong);
                    newMark.setExamTypeId(examTypeId);
                    newMark.setMarks(marksValue);
                    newMark.setMaxMarks(maxMarksValue);
                    newMark.setRemarks(remarks);
                    newMark.setEnteredByTeacherId(teacherId);

                    // Get student's semester
                    try {
                        StudentDTO student = studentService.getStudentById(studentId);
                        if (student != null) {
                            newMark.setSemester(student.getSemester());
                        } else {
                            newMark.setSemester(1); // Default if student not found
                        }
                    } catch (Exception e) {
                        newMark.setSemester(1); // Default if error
                        logger.error("Error getting semester for student {}: {}", studentId, e.getMessage());
                    }

                    newMarksToCreate.add(newMark);

                    logger.info("Prepared new mark for student {} in course {}: {}", 
                               studentId, courseIdLong, marksValue);
                }
            }
        }

        // Update existing marks
        for (MarkDTO mark : existingMarksToUpdate) {
            try {
                markService.updateMark(mark.getId(), mark);
            } catch (Exception e) {
                logger.error("Error updating mark ID {}: {}", mark.getId(), e.getMessage());
            }
        }

        // Create new marks
        for (MarkDTO mark : newMarksToCreate) {
            try {
                markService.createMark(mark);
            } catch (Exception e) {
                logger.error("Error creating new mark for student {}: {}", mark.getStudentId(), e.getMessage());
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
        return "teacher/student-marks";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        TeacherDTO teacher = teacherService.getTeacherByUsername(username);
        model.addAttribute("teacher", teacher);
        return "teacher/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute @Valid TeacherDTO teacherDTO,
                              BindingResult result,
                              @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "teacher/profile";
        }

        try {
            if (profilePicture != null && !profilePicture.isEmpty()) {
                teacherDTO.setProfilePicture(profilePicture.getBytes());
                teacherDTO.setProfilePictureContentType(profilePicture.getContentType());
            }

            teacherService.updateTeacher(teacherDTO.getId(), teacherDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/teacher/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            return "redirect:/teacher/profile";
        }
    }
}
