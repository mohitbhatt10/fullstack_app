package com.school.controller;

import com.school.dto.PasswordResetDto;
import com.school.exception.InvalidTokenException;
import com.school.entity.User;
import com.school.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            String appUrl = request.getScheme() + "://" + request.getServerName() + 
                        (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                        request.getContextPath();

            passwordResetService.createPasswordResetTokenForUser(email, appUrl);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "A password reset link has been sent to your email address.");
        } catch (UsernameNotFoundException e) {
            // We don't want to reveal which emails exist in the system
            redirectAttributes.addFlashAttribute("successMessage", 
                    "If your email exists in our system, you will receive a password reset link shortly.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "An error occurred while processing your request. Please try again.");
        }

        return "redirect:/forgot-password?sent";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        try {
            passwordResetService.validatePasswordResetToken(token);
            model.addAttribute("token", token);
            model.addAttribute("passwordResetDto", new PasswordResetDto());
            return "reset-password";
        } catch (InvalidTokenException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/invalid-token";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @ModelAttribute("passwordResetDto") @Valid PasswordResetDto passwordResetDto,
            BindingResult result,
            HttpServletRequest request,
            @RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("token", token);
            return "reset-password";
        }

        String appUrl = request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                request.getContextPath();


        try {
            User user = passwordResetService.validatePasswordResetToken(token);

            if (!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())) {
                model.addAttribute("token", token);
                model.addAttribute("errorMessage", "Passwords do not match");
                return "reset-password";
            }

            passwordResetService.changeUserPassword(user, passwordResetDto.getNewPassword(),appUrl);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Your password has been changed successfully. You can now login with your new password.");
            return "redirect:/login";
        } catch (InvalidTokenException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/invalid-token";
        }
    }
}
