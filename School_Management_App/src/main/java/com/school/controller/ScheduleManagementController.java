package com.school.controller;

import com.school.dto.CourseScheduleDTO;
import com.school.service.CourseScheduleService;
import com.school.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/schedules")
public class ScheduleManagementController {
    
    
    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;

    public ScheduleManagementController(CourseScheduleService courseScheduleService,
                                      CourseService courseService) {
        this.courseScheduleService = courseScheduleService;
        this.courseService = courseService;
    }

    @GetMapping
    public String showScheduleManagement(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/schedules/manage";
    }

    @GetMapping("/{courseId}")
    public String showCourseSchedule(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
        model.addAttribute("schedule", new CourseScheduleDTO());
        return "admin/schedules/schedule";
    }

    @GetMapping("/{courseId}/edit/{scheduleId}")
    public String showEditForm(@PathVariable Long courseId, 
                             @PathVariable Long scheduleId, 
                             Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
        model.addAttribute("schedule", courseScheduleService.getScheduleById(scheduleId));
        return "admin/schedules/schedule";
    }

    @PostMapping("/{courseId}")
    public String addOrUpdateSchedule(@PathVariable Long courseId,
                                    @Valid @ModelAttribute("schedule") CourseScheduleDTO schedule,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("course", courseService.getCourseById(courseId));
            model.addAttribute("schedules", courseScheduleService.getSchedulesByCourseId(courseId));
            return "admin/schedules/schedule";
        }

        try {
            schedule.setCourseId(courseId);
            
            if (schedule.getId() != null) {
                courseScheduleService.updateSchedule(schedule.getId(), schedule);
                redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully");
            } else {
                courseScheduleService.createSchedule(schedule);
                redirectAttributes.addFlashAttribute("successMessage", "Schedule added successfully");
            }
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/schedules/" + courseId;
    }

    @PostMapping("/{courseId}/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable Long courseId,
                               @PathVariable Long scheduleId,
                               RedirectAttributes redirectAttributes) {
        try {
            courseScheduleService.deleteSchedule(scheduleId);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/schedules/" + courseId;
    }

    @GetMapping("/classroom/{classroom}")
    public String getSchedulesByClassroom(@PathVariable String classroom,
                                        Model model) {
        model.addAttribute("schedules", courseScheduleService.getSchedulesByClassroom(classroom));
        model.addAttribute("selectedClassroom", classroom);
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/schedules/manage";
    }

    @GetMapping("/{courseId}/export")
    public String exportSchedule(@PathVariable Long courseId,
                               RedirectAttributes redirectAttributes) {
        try {
            courseScheduleService.exportScheduleToPdf(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule exported successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to export schedule: " + ex.getMessage());
        }
        return "redirect:/admin/schedules/" + courseId;
    }
} 