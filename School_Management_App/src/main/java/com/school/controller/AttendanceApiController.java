package com.school.controller;

import com.school.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {
    private final AttendanceService attendanceService;

    public AttendanceApiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/time-window")
    public ResponseEntity<Map<String, LocalTime>> getAttendanceTimeWindow(
            @RequestParam Long courseId,
            @RequestParam String date) {
        
        Map<String, LocalTime> timeWindow = attendanceService.getAttendanceTimeWindow(
            courseId, 
            LocalDate.parse(date)
        );

        if (timeWindow == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(timeWindow);
    }
}
