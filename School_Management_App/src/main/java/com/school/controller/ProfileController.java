package com.school.controller;

import com.school.entity.User;
import com.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String userProfile(Model model, Principal principal) {
        User user = userService.findEntityByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile/view";
    }

    @PostMapping("/picture")
    public String updateProfilePicture(@RequestParam("file") MultipartFile file, Principal principal) {
        userService.updateProfilePicture(principal.getName(), file);
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Principal principal, Model model) {
        boolean changed = userService.changePassword(principal.getName(), oldPassword, newPassword);
        if (!changed) {
            model.addAttribute("errorMessage", "Current password is incorrect.");
            return "profile/change-password";
        }
        model.addAttribute("successMessage", "Password changed successfully.");
        return "redirect:/profile";
    }
}
