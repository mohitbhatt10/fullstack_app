package com.school.controller;

import com.school.dto.AttendanceDTO;
import com.school.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceRestController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AttendanceRestController.class);
    
    private final AttendanceService attendanceService;

    public AttendanceRestController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Checks if attendance records exist for a given course, schedule, and date
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAttendanceExists(
            @RequestParam Long courseId,
            @RequestParam Long scheduleId,
            @RequestParam String date) {
        
        LocalDate attendanceDate = LocalDate.parse(date);
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean exists = attendanceService.existsAttendanceForCourseScheduleAndDate(
                courseId, scheduleId, attendanceDate);
            
            response.put("exists", exists);
            
            if (exists) {
                List<AttendanceDTO> records = attendanceService.getAttendanceByCourseScheduleAndDate(
                    courseId, scheduleId, attendanceDate);
                
                int presentCount = (int) records.stream()
                    .filter(AttendanceDTO::getPresent)
                    .count();
                
                response.put("count", records.size());
                response.put("presentCount", presentCount);
                response.put("absentCount", records.size() - presentCount);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking attendance: {}", e.getMessage());
            response.put("error", "Failed to check attendance records");
            response.put("exists", false);
            return ResponseEntity.ok(response);
        }
    }
}
