package com.school.controller;

import com.school.dto.*;
import com.school.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;

    @GetMapping
    public String reportsHome(Model model) {
        log.info("Accessing reports home page");
        
        // Add basic statistics for the dashboard
        model.addAttribute("totalStudents", studentService.getStudentCount());
        model.addAttribute("totalTeachers", teacherService.getTeacherCount());
        model.addAttribute("totalCourses", courseService.getCourseCount());
        
        // Add filter options
        model.addAttribute("reportTypes", reportService.getAvailableReportTypes());
        model.addAttribute("departments", reportService.getAvailableDepartments());
        model.addAttribute("semesters", reportService.getAvailableSemesters());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        
        // Initialize filter object
        model.addAttribute("filter", new ReportFilterDTO());
        
        return "admin/reports/index";
    }

    @PostMapping("/generate")
    public String generateReport(@ModelAttribute ReportFilterDTO filter, Model model) {
        log.info("Generating report with filter: {}", filter);
        
        try {
            switch (filter.getReportType()) {
                case "STUDENT_REPORT":
                    return generateStudentReport(filter, model);
                case "TEACHER_REPORT":
                    return generateTeacherReport(filter, model);
                case "COURSE_REPORT":
                    return generateCourseReport(filter, model);
                case "ATTENDANCE_REPORT":
                    return generateAttendanceReport(filter, model);
                case "ACADEMIC_PERFORMANCE":
                    return generateAcademicPerformanceReport(filter, model);
                case "DEPARTMENT_SUMMARY":
                    return generateDepartmentSummaryReport(filter, model);
                default:
                    model.addAttribute("errorMessage", "Invalid report type selected");
                    return addCommonAttributes(model, filter);
            }
        } catch (Exception e) {
            log.error("Error generating report", e);
            model.addAttribute("errorMessage", "Error generating report: " + e.getMessage());
            return addCommonAttributes(model, filter);
        }
    }

    private String generateStudentReport(ReportFilterDTO filter, Model model) {
        List<StudentReportDTO> students = reportService.generateStudentReport(filter);
        model.addAttribute("students", students);
        model.addAttribute("reportType", "STUDENT_REPORT");
        model.addAttribute("filter", filter);
        addCommonAttributes(model, filter);
        return "admin/reports/student-report";
    }

    private String generateTeacherReport(ReportFilterDTO filter, Model model) {
        List<TeacherReportDTO> teachers = reportService.generateTeacherReport(filter);
        model.addAttribute("teachers", teachers);
        model.addAttribute("reportType", "TEACHER_REPORT");
        model.addAttribute("filter", filter);
        addCommonAttributes(model, filter);
        return "admin/reports/teacher-report";
    }

    private String generateCourseReport(ReportFilterDTO filter, Model model) {
        List<CourseReportDTO> courses = reportService.generateCourseReport(filter);
        model.addAttribute("courses", courses);
        model.addAttribute("reportType", "COURSE_REPORT");
        model.addAttribute("filter", filter);
        addCommonAttributes(model, filter);
        return "admin/reports/course-report";
    }

    private String generateAttendanceReport(ReportFilterDTO filter, Model model) {
        if (filter.getCourseId() != null) {
            // Single course attendance report
            AttendanceReportDTO report = reportService.generateAttendanceReport(filter);
            model.addAttribute("attendanceReport", report);
            model.addAttribute("singleCourse", true);
        } else {
            // Multiple courses attendance report
            List<AttendanceReportDTO> reports = reportService.generateAttendanceReportByCourse(filter);
            model.addAttribute("attendanceReports", reports);
            model.addAttribute("singleCourse", false);
        }
        model.addAttribute("reportType", "ATTENDANCE_REPORT");
        model.addAttribute("filter", filter);
        addCommonAttributes(model, filter);
        return "admin/reports/attendance-report";
    }

    private String generateAcademicPerformanceReport(ReportFilterDTO filter, Model model) {
        List<StudentPerformanceSummaryDTO> performances = reportService.generateAcademicPerformanceReport(filter);
        model.addAttribute("performances", performances);
        model.addAttribute("reportType", "ACADEMIC_PERFORMANCE");
        model.addAttribute("filter", filter);
        addCommonAttributes(model, filter);
        return "admin/reports/academic-performance-report";
    }

    private String generateDepartmentSummaryReport(ReportFilterDTO filter, Model model) {
        DashboardStatsDTO stats = reportService.generateDepartmentReport(filter.getDepartment());
        model.addAttribute("departmentStats", stats);
        model.addAttribute("selectedDepartment", filter.getDepartment());
        model.addAttribute("reportType", "DEPARTMENT_SUMMARY");
        model.addAttribute("filter", filter);
        addCommonAttributes(model, filter);
        return "admin/reports/department-summary-report";
    }

    // Export endpoints
    @PostMapping("/export/students")
    public ResponseEntity<ByteArrayResource> exportStudentReport(
            @ModelAttribute ReportFilterDTO filter) {
        log.info("Exporting student report with filter: {}", filter);
        
        List<StudentReportDTO> students = reportService.generateStudentReport(filter);
        ByteArrayResource resource;
        String filename;
        String contentType;
        
        if ("PDF".equalsIgnoreCase(filter.getFormat())) {
            resource = reportService.exportStudentReportToPDF(students);
            filename = "student-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
            contentType = "application/pdf";
        } else {
            resource = reportService.exportStudentReportToExcel(students);
            filename = "student-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PostMapping("/export/teachers")
    public ResponseEntity<ByteArrayResource> exportTeacherReport(
            @ModelAttribute ReportFilterDTO filter) {
        log.info("Exporting teacher report with filter: {}", filter);
        
        List<TeacherReportDTO> teachers = reportService.generateTeacherReport(filter);
        ByteArrayResource resource;
        String filename;
        String contentType;
        
        if ("PDF".equalsIgnoreCase(filter.getFormat())) {
            resource = reportService.exportTeacherReportToPDF(teachers);
            filename = "teacher-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
            contentType = "application/pdf";
        } else {
            resource = reportService.exportTeacherReportToExcel(teachers);
            filename = "teacher-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PostMapping("/export/courses")
    public ResponseEntity<ByteArrayResource> exportCourseReport(
            @ModelAttribute ReportFilterDTO filter) {
        log.info("Exporting course report with filter: {}", filter);
        
        List<CourseReportDTO> courses = reportService.generateCourseReport(filter);
        ByteArrayResource resource;
        String filename;
        String contentType;
        
        if ("PDF".equalsIgnoreCase(filter.getFormat())) {
            resource = reportService.exportCourseReportToPDF(courses);
            filename = "course-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
            contentType = "application/pdf";
        } else {
            resource = reportService.exportCourseReportToExcel(courses);
            filename = "course-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PostMapping("/export/attendance")
    public ResponseEntity<ByteArrayResource> exportAttendanceReport(
            @ModelAttribute ReportFilterDTO filter) {
        log.info("Exporting attendance report with filter: {}", filter);
        
        AttendanceReportDTO attendanceReport = reportService.generateAttendanceReport(filter);
        ByteArrayResource resource;
        String filename;
        String contentType;
        
        if ("PDF".equalsIgnoreCase(filter.getFormat())) {
            resource = reportService.exportAttendanceReportToPDF(attendanceReport);
            filename = "attendance-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
            contentType = "application/pdf";
        } else {
            resource = reportService.exportAttendanceReportToExcel(attendanceReport);
            filename = "attendance-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    // Individual report endpoints
    @GetMapping("/student/{id}")
    public String individualStudentReport(@PathVariable Long id, Model model) {
        log.info("Generating individual student report for ID: {}", id);
        
        StudentReportDTO student = reportService.generateIndividualStudentReport(id);
        model.addAttribute("student", student);
        model.addAttribute("reportType", "INDIVIDUAL_STUDENT");
        
        return "admin/reports/individual-student-report";
    }

    @GetMapping("/teacher/{id}")
    public String individualTeacherReport(@PathVariable Long id, Model model) {
        log.info("Generating individual teacher report for ID: {}", id);
        
        TeacherReportDTO teacher = reportService.generateIndividualTeacherReport(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("reportType", "INDIVIDUAL_TEACHER");
        
        return "admin/reports/individual-teacher-report";
    }

    @GetMapping("/course/{id}")
    public String individualCourseReport(@PathVariable Long id, Model model) {
        log.info("Generating individual course report for ID: {}", id);
        
        CourseReportDTO course = reportService.generateIndividualCourseReport(id);
        model.addAttribute("course", course);
        model.addAttribute("reportType", "INDIVIDUAL_COURSE");
        
        return "admin/reports/individual-course-report";
    }

    // Quick report endpoints
    @GetMapping("/quick/attendance")
    public String quickAttendanceReport(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        log.info("Generating quick attendance report");
        
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setDepartment(department);
        filter.setSemester(semester);
        filter.setStartDate(startDate != null ? startDate : LocalDate.now().minusMonths(1));
        filter.setEndDate(endDate != null ? endDate : LocalDate.now());
        
        AttendanceReportDTO report = reportService.generateAttendanceReport(filter);
        model.addAttribute("attendanceReport", report);
        model.addAttribute("filter", filter);
        model.addAttribute("reportType", "QUICK_ATTENDANCE");
        
        addCommonAttributes(model, filter);
        return "admin/reports/quick-attendance-report";
    }

    @GetMapping("/quick/performance")
    public String quickPerformanceReport(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer semester,
            Model model) {
        
        log.info("Generating quick performance report");
        
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setDepartment(department);
        filter.setSemester(semester);
        
        List<StudentPerformanceSummaryDTO> performances = reportService.generateAcademicPerformanceReport(filter);
        model.addAttribute("performances", performances);
        model.addAttribute("filter", filter);
        model.addAttribute("reportType", "QUICK_PERFORMANCE");
        
        addCommonAttributes(model, filter);
        return "admin/reports/quick-performance-report";
    }

    // API endpoints for AJAX requests
    @GetMapping("/api/courses")
    @ResponseBody
    public List<CourseDTO> getCoursesByDepartmentAndSemester(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer semester) {
        
        List<CourseDTO> courses = courseService.getAllCourses();
        
        if (department != null && !department.isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getDepartment().equals(department))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (semester != null) {
            courses = courses.stream()
                    .filter(c -> c.getSemester().equals(semester))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return courses;
    }

    @GetMapping("/api/teachers")
    @ResponseBody
    public List<TeacherDTO> getTeachersByDepartment(@RequestParam(required = false) String department) {
        List<TeacherDTO> teachers = teacherService.getAllTeachers();
        
        if (department != null && !department.isEmpty()) {
            teachers = teachers.stream()
                    .filter(t -> t.getDepartment().equals(department))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return teachers;
    }

    private String addCommonAttributes(Model model, ReportFilterDTO filter) {
        model.addAttribute("reportTypes", reportService.getAvailableReportTypes());
        model.addAttribute("departments", reportService.getAvailableDepartments());
        model.addAttribute("semesters", reportService.getAvailableSemesters());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("filter", filter);
        return "admin/reports/index";
    }
}