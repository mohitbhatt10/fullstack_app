package com.school.controller;

import com.school.dto.ProfileEditRequestDTO;
import com.school.entity.User;
import com.school.service.ProfileEditRequestService;
import com.school.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProfileEditRequestService profileEditRequestService;

    @GetMapping
    public String userProfile(Model model, Principal principal) {
        User user = userService.findEntityByUsername(principal.getName());
        
        // Get any pending profile edit requests for this user
        List<ProfileEditRequestDTO> pendingRequests = profileEditRequestService.getRequestsByUsername(user.getUsername());
        
        model.addAttribute("user", user);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("hasPendingRequest", !pendingRequests.isEmpty());
        model.addAttribute("profileEditRequest", new ProfileEditRequestDTO());
        
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

    @PostMapping("/edit-request")
    public String createEditRequest(@Valid @ModelAttribute ProfileEditRequestDTO requestDTO, 
                                  BindingResult bindingResult, 
                                  Principal principal, 
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            User user = userService.findEntityByUsername(principal.getName());
            List<ProfileEditRequestDTO> pendingRequests = profileEditRequestService.getRequestsByUsername(user.getUsername());
            
            model.addAttribute("user", user);
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("hasPendingRequest", !pendingRequests.isEmpty());
            model.addAttribute("profileEditRequest", requestDTO); // Re-add the DTO with validation errors
            model.addAttribute("showEditModal", true);
            return "profile/view";
        }

        try {
            profileEditRequestService.createRequest(requestDTO, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile edit request submitted successfully. An administrator will review your request.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to submit profile edit request: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }

    @PostMapping("/edit-request/{id}/cancel")
    public String cancelEditRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            ProfileEditRequestDTO request = profileEditRequestService.getRequestById(id);
            if (request != null && request.getUserName().equals(principal.getName())) {
                profileEditRequestService.deleteRequest(id);
                redirectAttributes.addFlashAttribute("successMessage", "Profile edit request cancelled successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only cancel your own requests.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel request: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
}
