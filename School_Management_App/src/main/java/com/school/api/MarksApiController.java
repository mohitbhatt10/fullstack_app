package com.school.api;

import com.school.dto.MarkDTO;
import com.school.service.MarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marks")
public class MarksApiController {

    private final MarkService markService;

    public MarksApiController(MarkService markService) {
        this.markService = markService;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkMarksExist(
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "examTypeId", required = false) Long examTypeId,
            @RequestParam(value = "examType", required = false) String examType) {

        // Get all marks for this course
        List<MarkDTO> allMarks = markService.getMarksByCourseId(courseId);

        boolean marksExist = false;
        int count = 0;

        // Check if any marks exist for the specified exam type
        for (MarkDTO mark : allMarks) {
            boolean matches = false;
            
            if (examTypeId != null && mark.getExamTypeId() != null) {
                matches = mark.getExamTypeId().equals(examTypeId);
            } else if (examType != null && mark.getExamTypeName() != null) {
                matches = mark.getExamTypeName().equals(examType);
            }
            
            if (matches) {
                marksExist = true;
                count++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("exists", marksExist);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
