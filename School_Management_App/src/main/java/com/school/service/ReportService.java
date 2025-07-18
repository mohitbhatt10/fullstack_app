package com.school.service;

import com.school.dto.*;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface ReportService {
    
    // Student Reports
    List<StudentReportDTO> generateStudentReport(ReportFilterDTO filter);
    StudentReportDTO generateIndividualStudentReport(Long studentId);
    
    // Teacher Reports
    List<TeacherReportDTO> generateTeacherReport(ReportFilterDTO filter);
    TeacherReportDTO generateIndividualTeacherReport(Long teacherId);
    
    // Course Reports
    List<CourseReportDTO> generateCourseReport(ReportFilterDTO filter);
    CourseReportDTO generateIndividualCourseReport(Long courseId);
    
    // Attendance Reports
    AttendanceReportDTO generateAttendanceReport(ReportFilterDTO filter);
    List<AttendanceReportDTO> generateAttendanceReportByCourse(ReportFilterDTO filter);
    
    // Academic Performance Reports
    List<StudentPerformanceSummaryDTO> generateAcademicPerformanceReport(ReportFilterDTO filter);
    List<CoursePerformanceDTO> generateCoursePerformanceReport(ReportFilterDTO filter);
    
    // Department Reports
    DashboardStatsDTO generateDepartmentReport(String department);
    
    // Export Functions
    ByteArrayResource exportStudentReportToExcel(List<StudentReportDTO> students);
    ByteArrayResource exportStudentReportToPDF(List<StudentReportDTO> students);
    
    ByteArrayResource exportTeacherReportToExcel(List<TeacherReportDTO> teachers);
    ByteArrayResource exportTeacherReportToPDF(List<TeacherReportDTO> teachers);
    
    ByteArrayResource exportCourseReportToExcel(List<CourseReportDTO> courses);
    ByteArrayResource exportCourseReportToPDF(List<CourseReportDTO> courses);
    
    ByteArrayResource exportAttendanceReportToExcel(AttendanceReportDTO attendanceReport);
    ByteArrayResource exportAttendanceReportToPDF(AttendanceReportDTO attendanceReport);
    
    // Utility methods
    List<String> getAvailableReportTypes();
    List<String> getAvailableDepartments();
    List<Integer> getAvailableSemesters();
}