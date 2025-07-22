package com.school.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.school.dto.*;
import com.school.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final MarkService markService;
    
    @Override
    public List<StudentReportDTO> generateStudentReport(ReportFilterDTO filter) {
        log.info("Generating student report with filter: {}", filter);
        
        List<StudentDTO> students = studentService.getAllStudents();
        
        // Apply filters
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            students = students.stream()
                    .filter(s -> s.getDepartment().equals(filter.getDepartment()))
                    .collect(Collectors.toList());
        }
        
        if (filter.getSemester() != null) {
            students = students.stream()
                    .filter(s -> s.getSemester().equals(filter.getSemester()))
                    .collect(Collectors.toList());
        }
        
        return students.stream()
                .map(this::convertToStudentReportDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public StudentReportDTO generateIndividualStudentReport(Long studentId) {
        log.info("Generating individual student report for ID: {}", studentId);
        StudentDTO student = studentService.getStudentById(studentId);
        return convertToStudentReportDTO(student);
    }
    
    @Override
    public List<TeacherReportDTO> generateTeacherReport(ReportFilterDTO filter) {
        log.info("Generating teacher report with filter: {}", filter);
        
        List<TeacherDTO> teachers = teacherService.getAllTeachers();
        
        // Apply filters
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            teachers = teachers.stream()
                    .filter(t -> t.getDepartment().equals(filter.getDepartment()))
                    .collect(Collectors.toList());
        }
        
        return teachers.stream()
                .map(this::convertToTeacherReportDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public TeacherReportDTO generateIndividualTeacherReport(Long teacherId) {
        log.info("Generating individual teacher report for ID: {}", teacherId);
        TeacherDTO teacher = teacherService.getTeacherById(teacherId);
        return convertToTeacherReportDTO(teacher);
    }
    
    @Override
    public List<CourseReportDTO> generateCourseReport(ReportFilterDTO filter) {
        log.info("Generating course report with filter: {}", filter);
        
        List<CourseDTO> courses = courseService.getAllCourses();
        
        // Apply filters
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getDepartment().equals(filter.getDepartment()))
                    .collect(Collectors.toList());
        }
        
        if (filter.getSemester() != null) {
            courses = courses.stream()
                    .filter(c -> c.getSemester().equals(filter.getSemester()))
                    .collect(Collectors.toList());
        }
        
        return courses.stream()
                .map(this::convertToCourseReportDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CourseReportDTO generateIndividualCourseReport(Long courseId) {
        log.info("Generating individual course report for ID: {}", courseId);
        CourseDTO course = courseService.getCourseById(courseId);
        return convertToCourseReportDTO(course);
    }
    
    @Override
    public AttendanceReportDTO generateAttendanceReport(ReportFilterDTO filter) {
        log.info("Generating attendance report with filter: {}", filter);
        
        AttendanceReportDTO report = new AttendanceReportDTO();
        report.setStartDate(filter.getStartDate() != null ? filter.getStartDate() : LocalDate.now().minusMonths(1));
        report.setEndDate(filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now());
        report.setDepartment(filter.getDepartment());
        report.setSemester(filter.getSemester());
        
        // Get attendance data based on filters
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByDateBetween(
                report.getStartDate(), report.getEndDate());
        
        // Apply additional filters
        if (filter.getDepartment() != null || filter.getSemester() != null || filter.getCourseId() != null) {
            attendances = attendances.stream()
                    .filter(a -> filterAttendance(a, filter))
                    .collect(Collectors.toList());
        }
        
        // Calculate summary statistics
        report.setTotalClasses(attendances.size());
        report.setTotalPresent((int) attendances.stream().filter(AttendanceDTO::getPresent).count());
        report.setTotalAbsent(report.getTotalClasses() - report.getTotalPresent());
        report.setOverallAttendancePercentage(
                report.getTotalClasses() > 0 ? 
                (double) report.getTotalPresent() / report.getTotalClasses() * 100 : 0.0);
        
        // Generate student-wise attendance
        Map<Long, List<AttendanceDTO>> studentAttendanceMap = attendances.stream()
                .collect(Collectors.groupingBy(AttendanceDTO::getStudentId));
        
        List<StudentAttendanceDTO> studentAttendances = studentAttendanceMap.entrySet().stream()
                .map(entry -> {
                    Long studentId = entry.getKey();
                    List<AttendanceDTO> studentAttendanceList = entry.getValue();
                    StudentDTO student = studentService.getStudentById(studentId);
                    
                    int attended = (int) studentAttendanceList.stream()
                            .filter(AttendanceDTO::getPresent).count();
                    int total = studentAttendanceList.size();
                    double percentage = total > 0 ? (double) attended / total * 100 : 0.0;
                    
                    StudentAttendanceDTO dto = new StudentAttendanceDTO();
                    dto.setStudentId(student.getId());
                    dto.setRollNumber(student.getRollNumber());
                    dto.setFirstName(student.getFirstName());
                    dto.setLastName(student.getLastName());
                    dto.setClassesAttended(attended);
                    dto.setTotalClasses(total);
                    dto.setAttendancePercentage(percentage);
                    dto.setAttendanceStatus(getAttendanceStatus(percentage));
                    
                    return dto;
                })
                .collect(Collectors.toList());
        
        report.setStudentAttendances(studentAttendances);
        report.setTotalStudents(studentAttendances.size());
        
        // Generate daily attendance summary
        Map<LocalDate, List<AttendanceDTO>> dailyAttendanceMap = attendances.stream()
                .collect(Collectors.groupingBy(AttendanceDTO::getDate));
        
        List<DailyAttendanceDTO> dailyAttendances = dailyAttendanceMap.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<AttendanceDTO> dayAttendances = entry.getValue();
                    
                    int present = (int) dayAttendances.stream()
                            .filter(AttendanceDTO::getPresent).count();
                    int total = dayAttendances.size();
                    double percentage = total > 0 ? (double) present / total * 100 : 0.0;
                    
                    return new DailyAttendanceDTO(date, total, present, total - present, percentage);
                })
                .sorted(Comparator.comparing(DailyAttendanceDTO::getDate))
                .collect(Collectors.toList());
        
        report.setDailyAttendances(dailyAttendances);
        
        return report;
    }
    
    @Override
    public List<AttendanceReportDTO> generateAttendanceReportByCourse(ReportFilterDTO filter) {
        log.info("Generating attendance report by course with filter: {}", filter);
        
        List<CourseDTO> courses = courseService.getAllCourses();
        
        // Apply course filters
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getDepartment().equals(filter.getDepartment()))
                    .collect(Collectors.toList());
        }
        
        if (filter.getSemester() != null) {
            courses = courses.stream()
                    .filter(c -> c.getSemester().equals(filter.getSemester()))
                    .collect(Collectors.toList());
        }
        
        return courses.stream()
                .map(course -> {
                    ReportFilterDTO courseFilter = new ReportFilterDTO();
                    courseFilter.setCourseId(course.getId());
                    courseFilter.setStartDate(filter.getStartDate());
                    courseFilter.setEndDate(filter.getEndDate());
                    
                    AttendanceReportDTO report = generateAttendanceReport(courseFilter);
                    report.setCourseName(course.getName());
                    report.setCourseCode(course.getCode());
                    
                    return report;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StudentPerformanceSummaryDTO> generateAcademicPerformanceReport(ReportFilterDTO filter) {
        log.info("Generating academic performance report with filter: {}", filter);
        
        List<StudentDTO> students = studentService.getAllStudents();
        
        // Apply filters
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            students = students.stream()
                    .filter(s -> s.getDepartment().equals(filter.getDepartment()))
                    .collect(Collectors.toList());
        }
        
        if (filter.getSemester() != null) {
            students = students.stream()
                    .filter(s -> s.getSemester().equals(filter.getSemester()))
                    .collect(Collectors.toList());
        }
        
        return students.stream()
                .map(student -> {
                    List<MarkDTO> marks = markService.getMarksByStudentId(student.getId());
                    List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentId(student.getId());
                    
                    double averageMarks = marks.stream()
                            .mapToDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                            .average()
                            .orElse(0.0);
                    
                    double attendancePercentage = calculateAttendancePercentage(attendances);
                    String grade = calculateGrade(averageMarks);
                    String status = averageMarks >= 40 ? "PASS" : "FAIL";
                    
                    return new StudentPerformanceSummaryDTO(
                            student.getId(),
                            student.getRollNumber(),
                            student.getFirstName(),
                            student.getLastName(),
                            averageMarks,
                            attendancePercentage,
                            grade,
                            status
                    );
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CoursePerformanceDTO> generateCoursePerformanceReport(ReportFilterDTO filter) {
        log.info("Generating course performance report with filter: {}", filter);
        
        List<CourseDTO> courses = courseService.getAllCourses();
        
        // Apply filters
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getDepartment().equals(filter.getDepartment()))
                    .collect(Collectors.toList());
        }
        
        if (filter.getSemester() != null) {
            courses = courses.stream()
                    .filter(c -> c.getSemester().equals(filter.getSemester()))
                    .collect(Collectors.toList());
        }
        
        return courses.stream()
                .map(course -> {
                    List<MarkDTO> marks = markService.getMarksByCourseId(course.getId());
                    List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseId(course.getId());
                    
                    double averageMarks = marks.stream()
                            .mapToDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                            .average()
                            .orElse(0.0);
                    
                    double attendancePercentage = calculateAttendancePercentage(attendances);
                    String grade = calculateGrade(averageMarks);
                    String status = averageMarks >= 40 ? "PASS" : "FAIL";
                    
                    CoursePerformanceDTO dto = new CoursePerformanceDTO();
                    dto.setCourseId(course.getId());
                    dto.setCourseName(course.getName());
                    dto.setCourseCode(course.getCode());
                    dto.setDepartment(course.getDepartment());
                    dto.setSemester(course.getSemester());
                    dto.setAverageMarks(averageMarks);
                    dto.setAttendancePercentage(attendancePercentage);
                    dto.setGrade(grade);
                    dto.setStatus(status);
                    
                    // Calculate attendance details
                    Map<Long, List<AttendanceDTO>> studentAttendanceMap = attendances.stream()
                            .collect(Collectors.groupingBy(AttendanceDTO::getStudentId));
                    
                    int totalClasses = studentAttendanceMap.values().stream()
                            .mapToInt(List::size)
                            .max()
                            .orElse(0);
                    
                    int classesAttended = (int) attendances.stream()
                            .filter(AttendanceDTO::getPresent)
                            .count();
                    
                    dto.setClassesAttended(classesAttended);
                    dto.setTotalClasses(totalClasses);
                    
                    // Set teacher names - convert String to List
                    if (course.getTeacherNames() != null && !course.getTeacherNames().isEmpty()) {
                        dto.setTeacherNames(Arrays.asList(course.getTeacherNames().split(",")));
                    } else {
                        dto.setTeacherNames(new ArrayList<>());
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public DashboardStatsDTO generateDepartmentReport(String department) {
        log.info("Generating department report for: {}", department);
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Get counts for the specific department
        List<StudentDTO> students = studentService.getAllStudents().stream()
                .filter(s -> department == null || s.getDepartment().equals(department))
                .collect(Collectors.toList());
        
        List<TeacherDTO> teachers = teacherService.getAllTeachers().stream()
                .filter(t -> department == null || t.getDepartment().equals(department))
                .collect(Collectors.toList());
        
        List<CourseDTO> courses = courseService.getAllCourses().stream()
                .filter(c -> department == null || c.getDepartment().equals(department))
                .collect(Collectors.toList());
        
        stats.setStudentCount(students.size());
        stats.setTeacherCount(teachers.size());
        stats.setCourseCount(courses.size());
        
        return stats;
    }
    
    // Export methods
    @Override
    public ByteArrayResource exportStudentReportToExcel(List<StudentReportDTO> students) {
        log.info("Exporting {} students to Excel", students.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Student Report");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Roll Number", "First Name", "Last Name", "Email", "Department", 
                "Semester", "Overall Average", "Attendance %", "Total Courses", 
                "Passed Courses", "Failed Courses", "Performance Grade"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            for (int i = 0; i < students.size(); i++) {
                Row row = sheet.createRow(i + 1);
                StudentReportDTO student = students.get(i);
                
                row.createCell(0).setCellValue(student.getRollNumber());
                row.createCell(1).setCellValue(student.getFirstName());
                row.createCell(2).setCellValue(student.getLastName());
                row.createCell(3).setCellValue(student.getEmail());
                row.createCell(4).setCellValue(student.getDepartment());
                row.createCell(5).setCellValue(student.getSemester());
                row.createCell(6).setCellValue(student.getOverallAverage() != null ? student.getOverallAverage() : 0.0);
                row.createCell(7).setCellValue(student.getAttendancePercentage() != null ? student.getAttendancePercentage() : 0.0);
                row.createCell(8).setCellValue(student.getTotalCourses() != null ? student.getTotalCourses() : 0);
                row.createCell(9).setCellValue(student.getPassedCourses() != null ? student.getPassedCourses() : 0);
                row.createCell(10).setCellValue(student.getFailedCourses() != null ? student.getFailedCourses() : 0);
                row.createCell(11).setCellValue(student.getPerformanceGrade() != null ? student.getPerformanceGrade() : "N/A");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportStudentReportToPDF(List<StudentReportDTO> students) {
        log.info("Exporting {} students to PDF", students.size());
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Student Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Create table
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            
            // Add headers
            String[] headers = {
                "Roll Number", "Name", "Department", "Semester", 
                "Average", "Attendance %", "Courses", "Grade"
            };
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Add data rows
            for (StudentReportDTO student : students) {
                table.addCell(student.getRollNumber());
                table.addCell(student.getFirstName() + " " + student.getLastName());
                table.addCell(student.getDepartment());
                table.addCell(String.valueOf(student.getSemester()));
                table.addCell(String.format("%.2f", student.getOverallAverage() != null ? student.getOverallAverage() : 0.0));
                table.addCell(String.format("%.2f%%", student.getAttendancePercentage() != null ? student.getAttendancePercentage() : 0.0));
                table.addCell(String.valueOf(student.getTotalCourses() != null ? student.getTotalCourses() : 0));
                table.addCell(student.getPerformanceGrade() != null ? student.getPerformanceGrade() : "N/A");
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (DocumentException e) {
            log.error("Error creating PDF file", e);
            throw new RuntimeException("Failed to create PDF file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportTeacherReportToExcel(List<TeacherReportDTO> teachers) {
        log.info("Exporting {} teachers to Excel", teachers.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Teacher Report");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Name", "Email", "Department", "Designation", "Specialization",
                "Total Courses", "Total Students", "Avg Attendance", "Avg Performance"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            for (int i = 0; i < teachers.size(); i++) {
                Row row = sheet.createRow(i + 1);
                TeacherReportDTO teacher = teachers.get(i);
                
                row.createCell(0).setCellValue(teacher.getFirstName() + " " + teacher.getLastName());
                row.createCell(1).setCellValue(teacher.getEmail());
                row.createCell(2).setCellValue(teacher.getDepartment());
                row.createCell(3).setCellValue(teacher.getDesignation());
                row.createCell(4).setCellValue(teacher.getSpecialization());
                row.createCell(5).setCellValue(teacher.getTotalCourses() != null ? teacher.getTotalCourses() : 0);
                row.createCell(6).setCellValue(teacher.getTotalStudents() != null ? teacher.getTotalStudents() : 0);
                row.createCell(7).setCellValue(teacher.getAverageClassAttendance() != null ? teacher.getAverageClassAttendance() : 0.0);
                row.createCell(8).setCellValue(teacher.getAverageStudentPerformance() != null ? teacher.getAverageStudentPerformance() : 0.0);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportTeacherReportToPDF(List<TeacherReportDTO> teachers) {
        log.info("Exporting {} teachers to PDF", teachers.size());
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Teacher Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Create table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            // Add headers
            String[] headers = {
                "Name", "Department", "Designation", "Courses", "Students", "Avg Performance"
            };
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Add data rows
            for (TeacherReportDTO teacher : teachers) {
                table.addCell(teacher.getFirstName() + " " + teacher.getLastName());
                table.addCell(teacher.getDepartment());
                table.addCell(teacher.getDesignation());
                table.addCell(String.valueOf(teacher.getTotalCourses() != null ? teacher.getTotalCourses() : 0));
                table.addCell(String.valueOf(teacher.getTotalStudents() != null ? teacher.getTotalStudents() : 0));
                table.addCell(String.format("%.2f", teacher.getAverageStudentPerformance() != null ? teacher.getAverageStudentPerformance() : 0.0));
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (DocumentException e) {
            log.error("Error creating PDF file", e);
            throw new RuntimeException("Failed to create PDF file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportCourseReportToExcel(List<CourseReportDTO> courses) {
        log.info("Exporting {} courses to Excel", courses.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Course Report");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Course Code", "Course Name", "Department", "Semester", "Enrolled Students",
                "Average Marks", "Average Attendance", "Pass Percentage", "Teachers"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            for (int i = 0; i < courses.size(); i++) {
                Row row = sheet.createRow(i + 1);
                CourseReportDTO course = courses.get(i);
                
                row.createCell(0).setCellValue(course.getCode());
                row.createCell(1).setCellValue(course.getName());
                row.createCell(2).setCellValue(course.getDepartment());
                row.createCell(3).setCellValue(course.getSemester());
                row.createCell(4).setCellValue(course.getTotalEnrolledStudents() != null ? course.getTotalEnrolledStudents() : 0);
                row.createCell(5).setCellValue(course.getAverageMarks() != null ? course.getAverageMarks() : 0.0);
                row.createCell(6).setCellValue(course.getAverageAttendance() != null ? course.getAverageAttendance() : 0.0);
                row.createCell(7).setCellValue(course.getPassPercentage() != null ? course.getPassPercentage() : 0.0);
                row.createCell(8).setCellValue(course.getTeacherNames() != null ? String.join(", ", course.getTeacherNames()) : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportCourseReportToPDF(List<CourseReportDTO> courses) {
        log.info("Exporting {} courses to PDF", courses.size());
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Course Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Create table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            
            // Add headers
            String[] headers = {
                "Code", "Name", "Department", "Semester", "Students", "Avg Marks", "Pass %"
            };
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Add data rows
            for (CourseReportDTO course : courses) {
                table.addCell(course.getCode());
                table.addCell(course.getName());
                table.addCell(course.getDepartment());
                table.addCell(String.valueOf(course.getSemester()));
                table.addCell(String.valueOf(course.getTotalEnrolledStudents() != null ? course.getTotalEnrolledStudents() : 0));
                table.addCell(String.format("%.2f", course.getAverageMarks() != null ? course.getAverageMarks() : 0.0));
                table.addCell(String.format("%.2f%%", course.getPassPercentage() != null ? course.getPassPercentage() : 0.0));
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (DocumentException e) {
            log.error("Error creating PDF file", e);
            throw new RuntimeException("Failed to create PDF file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportAttendanceReportToExcel(AttendanceReportDTO attendanceReport) {
        log.info("Exporting attendance report to Excel");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Add summary information
            Row summaryRow1 = sheet.createRow(0);
            summaryRow1.createCell(0).setCellValue("Attendance Report Summary");
            summaryRow1.getCell(0).setCellStyle(headerStyle);
            
            Row summaryRow2 = sheet.createRow(1);
            summaryRow2.createCell(0).setCellValue("Period:");
            summaryRow2.createCell(1).setCellValue(attendanceReport.getStartDate() + " to " + attendanceReport.getEndDate());
            
            Row summaryRow3 = sheet.createRow(2);
            summaryRow3.createCell(0).setCellValue("Overall Attendance:");
            summaryRow3.createCell(1).setCellValue(String.format("%.2f%%", attendanceReport.getOverallAttendancePercentage()));
            
            // Create student attendance table
            Row headerRow = sheet.createRow(5);
            String[] headers = {
                "Roll Number", "Student Name", "Classes Attended", "Total Classes", "Attendance %", "Status"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add student data
            List<StudentAttendanceDTO> students = attendanceReport.getStudentAttendances();
            for (int i = 0; i < students.size(); i++) {
                Row row = sheet.createRow(i + 6);
                StudentAttendanceDTO student = students.get(i);
                
                row.createCell(0).setCellValue(student.getRollNumber());
                row.createCell(1).setCellValue(student.getFirstName() + " " + student.getLastName());
                row.createCell(2).setCellValue(student.getClassesAttended());
                row.createCell(3).setCellValue(student.getTotalClasses());
                row.createCell(4).setCellValue(student.getAttendancePercentage());
                row.createCell(5).setCellValue(student.getAttendanceStatus());
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportAttendanceReportToPDF(AttendanceReportDTO attendanceReport) {
        log.info("Exporting attendance report to PDF");
        
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Attendance Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Add summary
            Paragraph summary = new Paragraph();
            summary.add(new Phrase("Period: " + attendanceReport.getStartDate() + " to " + attendanceReport.getEndDate() + "\n"));
            summary.add(new Phrase("Overall Attendance: " + String.format("%.2f%%", attendanceReport.getOverallAttendancePercentage()) + "\n"));
            summary.add(new Phrase("Total Students: " + attendanceReport.getTotalStudents() + "\n\n"));
            document.add(summary);
            
            // Create table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Add headers
            String[] headers = {
                "Roll Number", "Student Name", "Attended", "Total", "Percentage"
            };
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Add data rows
            for (StudentAttendanceDTO student : attendanceReport.getStudentAttendances()) {
                table.addCell(student.getRollNumber());
                table.addCell(student.getFirstName() + " " + student.getLastName());
                table.addCell(String.valueOf(student.getClassesAttended()));
                table.addCell(String.valueOf(student.getTotalClasses()));
                table.addCell(String.format("%.2f%%", student.getAttendancePercentage()));
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (DocumentException e) {
            log.error("Error creating PDF file", e);
            throw new RuntimeException("Failed to create PDF file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportPerformanceReportToExcel(List<StudentPerformanceSummaryDTO> performances) {
        log.info("Exporting {} student performances to Excel", performances.size());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Performance Report");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Roll Number", "First Name", "Last Name", "Average Marks", 
                "Attendance %", "Grade", "Status", "Performance Level"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            for (int i = 0; i < performances.size(); i++) {
                Row row = sheet.createRow(i + 1);
                StudentPerformanceSummaryDTO performance = performances.get(i);
                
                row.createCell(0).setCellValue(performance.getRollNumber());
                row.createCell(1).setCellValue(performance.getFirstName());
                row.createCell(2).setCellValue(performance.getLastName());
                row.createCell(3).setCellValue(performance.getAverageMarks() != null ? performance.getAverageMarks() : 0.0);
                row.createCell(4).setCellValue(performance.getAttendancePercentage() != null ? performance.getAttendancePercentage() : 0.0);
                row.createCell(5).setCellValue(performance.getGrade() != null ? performance.getGrade() : "N/A");
                row.createCell(6).setCellValue(performance.getStatus() != null ? performance.getStatus() : "N/A");
                
                // Calculate performance level
                String performanceLevel = "INSUFFICIENT DATA";
                if (performance.getAverageMarks() != null && performance.getAttendancePercentage() != null) {
                    if (performance.getAverageMarks() >= 80 && performance.getAttendancePercentage() >= 80) {
                        performanceLevel = "EXCELLENT";
                    } else if (performance.getAverageMarks() >= 60 && performance.getAttendancePercentage() >= 70) {
                        performanceLevel = "GOOD";
                    } else if (performance.getAverageMarks() >= 40 && performance.getAttendancePercentage() >= 60) {
                        performanceLevel = "AVERAGE";
                    } else {
                        performanceLevel = "NEEDS IMPROVEMENT";
                    }
                }
                row.createCell(7).setCellValue(performanceLevel);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }
    
    @Override
    public ByteArrayResource exportPerformanceReportToPDF(List<StudentPerformanceSummaryDTO> performances) {
        log.info("Exporting {} student performances to PDF", performances.size());
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Add title
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Student Performance Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Add summary
            if (!performances.isEmpty()) {
                double avgMarks = performances.stream()
                        .filter(p -> p.getAverageMarks() != null)
                        .mapToDouble(StudentPerformanceSummaryDTO::getAverageMarks)
                        .average()
                        .orElse(0.0);
                
                double avgAttendance = performances.stream()
                        .filter(p -> p.getAttendancePercentage() != null)
                        .mapToDouble(StudentPerformanceSummaryDTO::getAttendancePercentage)
                        .average()
                        .orElse(0.0);
                
                long passedStudents = performances.stream()
                        .filter(p -> "PASS".equals(p.getStatus()))
                        .count();
                
                Paragraph summary = new Paragraph();
                summary.add(new Phrase("Total Students: " + performances.size() + "\n"));
                summary.add(new Phrase("Average Marks: " + String.format("%.2f%%", avgMarks) + "\n"));
                summary.add(new Phrase("Average Attendance: " + String.format("%.2f%%", avgAttendance) + "\n"));
                summary.add(new Phrase("Pass Rate: " + String.format("%.2f%%", (double) passedStudents / performances.size() * 100) + "\n\n"));
                document.add(summary);
            }
            
            // Create table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            
            // Add headers
            String[] headers = {
                "Roll Number", "Student Name", "Avg Marks", "Attendance %", "Grade", "Status", "Level"
            };
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Add data rows
            for (StudentPerformanceSummaryDTO performance : performances) {
                table.addCell(performance.getRollNumber());
                table.addCell(performance.getFirstName() + " " + performance.getLastName());
                table.addCell(String.format("%.2f", performance.getAverageMarks() != null ? performance.getAverageMarks() : 0.0));
                table.addCell(String.format("%.2f%%", performance.getAttendancePercentage() != null ? performance.getAttendancePercentage() : 0.0));
                table.addCell(performance.getGrade() != null ? performance.getGrade() : "N/A");
                table.addCell(performance.getStatus() != null ? performance.getStatus() : "N/A");
                
                // Calculate performance level
                String performanceLevel = "INSUFFICIENT DATA";
                if (performance.getAverageMarks() != null && performance.getAttendancePercentage() != null) {
                    if (performance.getAverageMarks() >= 80 && performance.getAttendancePercentage() >= 80) {
                        performanceLevel = "EXCELLENT";
                    } else if (performance.getAverageMarks() >= 60 && performance.getAttendancePercentage() >= 70) {
                        performanceLevel = "GOOD";
                    } else if (performance.getAverageMarks() >= 40 && performance.getAttendancePercentage() >= 60) {
                        performanceLevel = "AVERAGE";
                    } else {
                        performanceLevel = "NEEDS IMPROVEMENT";
                    }
                }
                table.addCell(performanceLevel);
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (DocumentException e) {
            log.error("Error creating PDF file", e);
            throw new RuntimeException("Failed to create PDF file", e);
        }
    }
    
    @Override
    public List<String> getAvailableReportTypes() {
        return Arrays.asList(
            "STUDENT_REPORT",
            "TEACHER_REPORT", 
            "COURSE_REPORT",
            "ATTENDANCE_REPORT",
            "ACADEMIC_PERFORMANCE",
            "DEPARTMENT_SUMMARY"
        );
    }
    
    @Override
    public List<String> getAvailableDepartments() {
        return Arrays.asList("CSE", "ECE", "ME", "CE", "EEE", "IT");
    }
    
    @Override
    public List<Integer> getAvailableSemesters() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    }
    
    // Helper methods
    private StudentReportDTO convertToStudentReportDTO(StudentDTO student) {
        StudentReportDTO dto = new StudentReportDTO();
        dto.setId(student.getId());
        dto.setRollNumber(student.getRollNumber());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setDepartment(student.getDepartment());
        dto.setSemester(student.getSemester());
        // Note: UserDTO doesn't have createdAt, so we'll skip this
        
        // Calculate academic performance
        List<MarkDTO> marks = markService.getMarksByStudentId(student.getId());
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentId(student.getId());
        
        double overallAverage = marks.stream()
                .mapToDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                .average()
                .orElse(0.0);
        
        double attendancePercentage = calculateAttendancePercentage(attendances);
        
        dto.setOverallAverage(overallAverage);
        dto.setAttendancePercentage(attendancePercentage);
        dto.setPerformanceGrade(calculateGrade(overallAverage));
        dto.setAttendanceStatus(getAttendanceStatus(attendancePercentage));
        
        // Calculate course statistics
        Set<Long> courseIds = marks.stream().map(MarkDTO::getCourseId).collect(Collectors.toSet());
        dto.setTotalCourses(courseIds.size());
        
        Map<Long, Double> courseAverages = marks.stream()
                .collect(Collectors.groupingBy(
                    MarkDTO::getCourseId,
                    Collectors.averagingDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                ));
        
        dto.setPassedCourses((int) courseAverages.values().stream().filter(avg -> avg >= 40).count());
        dto.setFailedCourses((int) courseAverages.values().stream().filter(avg -> avg < 40).count());
        
        // Calculate attendance details
        int totalClassesAttended = (int) attendances.stream()
                .filter(AttendanceDTO::getPresent)
                .count();
        
        dto.setTotalClassesAttended(totalClassesAttended);
        dto.setTotalClassesHeld(attendances.size());
        
        return dto;
    }
    
    private TeacherReportDTO convertToTeacherReportDTO(TeacherDTO teacher) {
        TeacherReportDTO dto = new TeacherReportDTO();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setEmail(teacher.getEmail());
        dto.setDepartment(teacher.getDepartment());
        dto.setDesignation(teacher.getDesignation());
        dto.setSpecialization(teacher.getSpecialization());
        // Note: UserDTO doesn't have createdAt, so we'll skip this
        
        // Get teacher's courses and calculate statistics
        List<CourseDTO> courses = courseService.getAllCourses().stream()
                .filter(c -> c.getTeacherNames() != null && 
                           c.getTeacherNames().contains(teacher.getFirstName() + " " + teacher.getLastName()))
                .collect(Collectors.toList());
        
        dto.setTotalCourses(courses.size());
        
        // Calculate total students across all courses
        int totalStudents = courses.stream()
                .mapToInt(c -> studentService.getStudentsByCourseId(c.getId()).size())
                .sum();
        dto.setTotalStudents(totalStudents);
        
        // Calculate marks entered and attendance marked
        List<MarkDTO> marksEntered = markService.getAllMarks().stream()
                .filter(m -> m.getEnteredByTeacherId() != null && 
                           m.getEnteredByTeacherId().equals(teacher.getId()))
                .collect(Collectors.toList());
        dto.setMarksEntered(marksEntered.size());
        
        List<AttendanceDTO> attendanceMarked = attendanceService.getAllAttendance().stream()
                .filter(a -> a.getMarkedByTeacherId() != null && 
                           a.getMarkedByTeacherId().equals(teacher.getId()))
                .collect(Collectors.toList());
        dto.setAttendanceMarked(attendanceMarked.size());
        
        // Calculate average performance metrics
        if (!marksEntered.isEmpty()) {
            double avgPerformance = marksEntered.stream()
                    .mapToDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                    .average()
                    .orElse(0.0);
            dto.setAverageStudentPerformance(avgPerformance);
        }
        
        if (!attendanceMarked.isEmpty()) {
            double avgAttendance = attendanceMarked.stream()
                    .mapToDouble(a -> a.getPresent() ? 100.0 : 0.0)
                    .average()
                    .orElse(0.0);
            dto.setAverageClassAttendance(avgAttendance);
        }
        
        return dto;
    }
    
    private CourseReportDTO convertToCourseReportDTO(CourseDTO course) {
        CourseReportDTO dto = new CourseReportDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setCode(course.getCode());
        dto.setDepartment(course.getDepartment());
        dto.setSemester(course.getSemester());
        dto.setSyllabus(course.getSyllabus());
        
        // Convert String to List for teacher names
        if (course.getTeacherNames() != null && !course.getTeacherNames().isEmpty()) {
            dto.setTeacherNames(Arrays.asList(course.getTeacherNames().split(",")));
        } else {
            dto.setTeacherNames(new ArrayList<>());
        }
        
        // Get enrolled students
        List<StudentDTO> enrolledStudents = studentService.getStudentsByCourseId(course.getId());
        dto.setTotalEnrolledStudents(enrolledStudents.size());
        dto.setActiveStudents(enrolledStudents.size()); // Assuming all are active
        dto.setInactiveStudents(0);
        
        // Calculate performance statistics
        List<MarkDTO> marks = markService.getMarksByCourseId(course.getId());
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseId(course.getId());
        
        if (!marks.isEmpty()) {
            double averageMarks = marks.stream()
                    .mapToDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                    .average()
                    .orElse(0.0);
            dto.setAverageMarks(averageMarks);
            
            // Calculate pass/fail statistics
            Map<Long, Double> studentAverages = marks.stream()
                    .collect(Collectors.groupingBy(
                        MarkDTO::getStudentId,
                        Collectors.averagingDouble(m -> (m.getMarks() / m.getMaxMarks()) * 100)
                    ));
            
            int passedStudents = (int) studentAverages.values().stream().filter(avg -> avg >= 40).count();
            int failedStudents = (int) studentAverages.values().stream().filter(avg -> avg < 40).count();
            
            dto.setPassedStudents(passedStudents);
            dto.setFailedStudents(failedStudents);
            dto.setPassPercentage(studentAverages.size() > 0 ? 
                (double) passedStudents / studentAverages.size() * 100 : 0.0);
            
            // Calculate grade distribution
            dto.setGradeA((int) studentAverages.values().stream().filter(avg -> avg >= 90).count());
            dto.setGradeB((int) studentAverages.values().stream().filter(avg -> avg >= 80 && avg < 90).count());
            dto.setGradeC((int) studentAverages.values().stream().filter(avg -> avg >= 70 && avg < 80).count());
            dto.setGradeD((int) studentAverages.values().stream().filter(avg -> avg >= 40 && avg < 70).count());
            dto.setGradeF((int) studentAverages.values().stream().filter(avg -> avg < 40).count());
        }
        
        if (!attendances.isEmpty()) {
            double attendancePercentage = calculateAttendancePercentage(attendances);
            dto.setAverageAttendance(attendancePercentage);
        }
        
        return dto;
    }
    
    private boolean filterAttendance(AttendanceDTO attendance, ReportFilterDTO filter) {
        // Since AttendanceDTO uses IDs, we need to fetch the student and course data
        if (filter.getDepartment() != null) {
            StudentDTO student = studentService.getStudentById(attendance.getStudentId());
            if (!student.getDepartment().equals(filter.getDepartment())) {
                return false;
            }
        }
        
        if (filter.getSemester() != null) {
            StudentDTO student = studentService.getStudentById(attendance.getStudentId());
            if (!student.getSemester().equals(filter.getSemester())) {
                return false;
            }
        }
        
        if (filter.getCourseId() != null && 
            !attendance.getCourseId().equals(filter.getCourseId())) {
            return false;
        }
        
        return true;
    }
    
    private double calculateAttendancePercentage(List<AttendanceDTO> attendances) {
        if (attendances.isEmpty()) {
            return 0.0;
        }
        
        long presentCount = attendances.stream().filter(AttendanceDTO::getPresent).count();
        return (double) presentCount / attendances.size() * 100;
    }
    
    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
    
    private String getAttendanceStatus(double percentage) {
        if (percentage >= 90) return "EXCELLENT";
        if (percentage >= 75) return "GOOD";
        if (percentage >= 60) return "AVERAGE";
        return "POOR";
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
}