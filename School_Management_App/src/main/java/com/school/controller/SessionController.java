package com.school.controller;

import com.school.dto.SessionDTO;
import com.school.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public String listSessions(Model model) {
        model.addAttribute("academicSessions", sessionService.getAllSessions());
        return "admin/sessions/list";
    }

    @GetMapping("/new")
    public String newSessionForm(Model model) {
        model.addAttribute("session", new SessionDTO());
        return "admin/sessions/form";
    }

    @PostMapping
    public String createSession(@Valid @ModelAttribute("session") SessionDTO sessionDTO,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "admin/sessions/form";
        }

        sessionService.createSession(sessionDTO);
        return "redirect:/admin/sessions";
    }

    @GetMapping("/{id}/edit")
    public String editSessionForm(@PathVariable Long id, Model model) {
        model.addAttribute("session", sessionService.getSessionById(id));
        return "admin/sessions/form";
    }

    @GetMapping("/{id}")
    public String viewSession(@PathVariable Long id, Model model) {
        model.addAttribute("academicSession", sessionService.getSessionById(id));
        return "admin/sessions/view";
    }

    @PostMapping("/{id}")
    public String updateSession(@PathVariable Long id,
                              @Valid @ModelAttribute("session") SessionDTO sessionDTO,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "admin/sessions/form";
        }

        sessionService.updateSession(id, sessionDTO);
        return "redirect:/admin/sessions";
    }

    @PostMapping("/{id}/activate")
    @ResponseBody
    public SessionDTO activateSession(@PathVariable Long id) {
        return sessionService.activateSession(id);
    }
}
