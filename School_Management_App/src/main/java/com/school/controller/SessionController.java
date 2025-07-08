package com.school.controller;

import com.school.dto.SessionDTO;
import com.school.service.SessionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sessions")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
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
        model.addAttribute("academicSession", new SessionDTO());
        return "admin/sessions/form";
    }

    @PostMapping
    public String createSession(@Valid @ModelAttribute("academicSession") SessionDTO sessionDTO,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("academicSession", sessionDTO);
            return "admin/sessions/form";
        }

        logger.debug("Creating session with data: {}", sessionDTO);
        sessionService.createSession(sessionDTO);
        return "redirect:/admin/sessions";
    }

    @GetMapping("/{id}/edit")
    public String editSessionForm(@PathVariable Long id, Model model) {
        SessionDTO sessionDTO = sessionService.getSessionById(id);
        logger.info("Editing session: ID={}, Name={}, StartDate={}, EndDate={}", 
                   sessionDTO.getId(), sessionDTO.getName(), sessionDTO.getStartDate(), sessionDTO.getEndDate());
        model.addAttribute("academicSession", sessionDTO);
        return "admin/sessions/form";
    }

    @GetMapping("/{id}")
    public String viewSession(@PathVariable Long id, Model model) {
        model.addAttribute("academicSession", sessionService.getSessionById(id));
        return "admin/sessions/view";
    }

    @PostMapping("/{id}")
    public String updateSession(@PathVariable Long id,
                              @Valid @ModelAttribute("academicSession") SessionDTO sessionDTO,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("academicSession", sessionDTO);
            return "admin/sessions/form";
        }

        logger.debug("Updating session with id {}: {}", id, sessionDTO);
        sessionService.updateSession(id, sessionDTO);
        return "redirect:/admin/sessions";
    }

    @PostMapping("/{id}/activate")
    @ResponseBody
    public SessionDTO activateSession(@PathVariable Long id) {
        return sessionService.activateSession(id);
    }
}
