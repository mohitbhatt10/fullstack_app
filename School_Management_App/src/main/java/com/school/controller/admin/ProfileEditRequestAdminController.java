package com.school.controller.admin;

import com.school.dto.ProfileEditRequestDTO;
import com.school.service.ProfileEditRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/profile-requests")
@PreAuthorize("hasRole('ADMIN')")
public class ProfileEditRequestAdminController {

    @Autowired
    private ProfileEditRequestService profileEditRequestService;

    @GetMapping
    public String listRequests(Model model) {
        List<ProfileEditRequestDTO> allRequests = profileEditRequestService.getAllRequests();
        List<ProfileEditRequestDTO> pendingRequests = profileEditRequestService.getPendingRequests();
        
        model.addAttribute("allRequests", allRequests);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingCount", pendingRequests.size());
        
        return "admin/profile-requests/list";
    }

    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        ProfileEditRequestDTO request = profileEditRequestService.getRequestById(id);
        if (request == null) {
            return "redirect:/admin/profile-requests?error=Request not found";
        }
        
        model.addAttribute("request", request);
        return "admin/profile-requests/detail";
    }

    @PostMapping("/{id}/approve")
    public String approveRequest(@PathVariable Long id, 
                               @RequestParam(required = false) String adminComments,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            profileEditRequestService.approveRequest(id, principal.getName(), adminComments);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile edit request approved successfully. User profile has been updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to approve request: " + e.getMessage());
        }
        
        return "redirect:/admin/profile-requests";
    }

    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id, 
                              @RequestParam(required = false) String adminComments,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            profileEditRequestService.rejectRequest(id, principal.getName(), adminComments);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile edit request rejected successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to reject request: " + e.getMessage());
        }
        
        return "redirect:/admin/profile-requests";
    }

    @PostMapping("/{id}/delete")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            profileEditRequestService.deleteRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile edit request deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to delete request: " + e.getMessage());
        }
        
        return "redirect:/admin/profile-requests";
    }
}
