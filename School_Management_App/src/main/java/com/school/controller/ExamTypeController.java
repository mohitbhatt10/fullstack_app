package com.school.controller;

import com.school.dto.ExamTypeDTO;
import com.school.service.ExamTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/exam-types")
@RequiredArgsConstructor
public class ExamTypeController {

    private final ExamTypeService examTypeService;

    @GetMapping
    public String listExamTypes(Model model) {
        model.addAttribute("examTypes", examTypeService.getAllExamTypes());
        return "admin/exam-types/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("examType", new ExamTypeDTO());
        return "admin/exam-types/form";
    }

    @PostMapping
    public String createExamType(@Valid @ModelAttribute("examType") ExamTypeDTO examTypeDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/exam-types/form";
        }

        try {
            examTypeService.createExamType(examTypeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Exam type created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "admin/exam-types/form";
        }

        return "redirect:/admin/exam-types";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ExamTypeDTO examType = examTypeService.getExamTypeById(id);
            model.addAttribute("examType", examType);
            return "admin/exam-types/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/exam-types";
        }
    }

    @PostMapping("/{id}")
    public String updateExamType(@PathVariable Long id,
                                @Valid @ModelAttribute("examType") ExamTypeDTO examTypeDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/exam-types/form";
        }

        try {
            examTypeService.updateExamType(id, examTypeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Exam type updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "admin/exam-types/form";
        }

        return "redirect:/admin/exam-types";
    }

    @PostMapping("/{id}/delete")
    public String deleteExamType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examTypeService.deleteExamType(id);
            redirectAttributes.addFlashAttribute("successMessage", "Exam type deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/exam-types";
    }
}