package com.school.controller;

import com.school.dto.CourseScheduleDTO;
import com.school.service.CourseScheduleService;
import com.school.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/courses")
public class CourseScheduleController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseScheduleController.class);
    
    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;

    public CourseScheduleController(CourseScheduleService courseScheduleService,
                                  CourseService courseService) {
        this.courseScheduleService = courseScheduleService;
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}/schedule")
    public String showScheduleForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
        model.addAttribute("schedule", new CourseScheduleDTO());
        return "admin/courses/schedule";
    }

    @PostMapping("/{courseId}/schedule")
    public String addSchedule(@PathVariable Long courseId,
                            @Valid @ModelAttribute("schedule") CourseScheduleDTO schedule,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/courses/schedule";
        }

        try {
            schedule.setCourseId(courseId);
            courseScheduleService.createSchedule(schedule);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule added successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/courses/" + courseId + "/schedule";
    }

    @PostMapping("/{courseId}/schedule/{scheduleId}/delete")
    public String deleteSchedule(@PathVariable Long courseId,
                               @PathVariable Long scheduleId,
                               RedirectAttributes redirectAttributes) {
        try {
            courseScheduleService.deleteSchedule(scheduleId);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/courses/" + courseId + "/schedule";
    }
}
