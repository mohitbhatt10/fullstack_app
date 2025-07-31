package com.school.controller;

import com.school.entity.User;
import com.school.service.TwoFactorAuthService;
import com.school.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserService userService;

    @GetMapping("/2fa")
    public String show2FAPage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("pre_auth_username");
        
        if (username == null) {
            log.warn("No pre-auth username found in session");
            return "redirect:/login?error=session_expired";
        }

        User user = userService.findEntityByUsername(username);
        if (!twoFactorAuthService.isUserEligibleFor2FA(user)) {
            log.warn("User {} is not eligible for 2FA", username);
            return "redirect:/login?error=2fa_not_available";
        }

        // Send OTP
        try {
            twoFactorAuthService.sendLoginOtp(user);
            model.addAttribute("phoneNumber", maskPhoneNumber(user.getPhoneNumber()));
            model.addAttribute("username", username);
            return "auth/two-factor";
        } catch (Exception e) {
            log.error("Error sending OTP for user: {}", username, e);
            return "redirect:/login?error=otp_send_failed";
        }
    }

    @PostMapping("/2fa/verify")
    public String verify2FA(@RequestParam("otpCode") String otpCode,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        String username = (String) session.getAttribute("pre_auth_username");
        
        if (username == null) {
            log.warn("No pre-auth username found in session for 2FA verification");
            return "redirect:/login?error=session_expired";
        }

        try {
            User user = userService.findEntityByUsername(username);
            boolean verified = twoFactorAuthService.verifyLoginOtp(user, otpCode);

            if (verified) {
                // Clear pre-auth session data
                session.removeAttribute("pre_auth_username");
                
                // Create auth session
                String authSession = twoFactorAuthService.createAuthSession(user);
                session.setAttribute("auth_session", authSession);
                session.setAttribute("authenticated_user", user.getUsername());

                log.info("2FA verification successful for user: {}", username);
                
                // Redirect based on user role
                return redirectBasedOnRole(user);
            } else {
                log.warn("2FA verification failed for user: {}", username);
                redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP. Please try again.");
                return "redirect:/auth/2fa";
            }
        } catch (Exception e) {
            log.error("Error during 2FA verification for user: {}", username, e);
            redirectAttributes.addFlashAttribute("error", "Verification failed. Please try again.");
            return "redirect:/auth/2fa";
        }
    }

    @PostMapping("/2fa/resend")
    @ResponseBody
    public String resendOtp(HttpSession session) {
        String username = (String) session.getAttribute("pre_auth_username");
        
        if (username == null) {
            return "session_expired";
        }

        try {
            User user = userService.findEntityByUsername(username);
            twoFactorAuthService.sendLoginOtp(user);
            log.info("OTP resent for user: {}", username);
            return "success";
        } catch (Exception e) {
            log.error("Error resending OTP for user: {}", username, e);
            return "error";
        }
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        
        String lastFour = phoneNumber.substring(phoneNumber.length() - 4);
        return "****" + lastFour;
    }

    private String redirectBasedOnRole(User user) {
        return switch (user.getRole()) {
            case ADMIN -> "redirect:/admin/dashboard";
            case TEACHER -> "redirect:/teacher/dashboard";
            case STUDENT -> "redirect:/student/dashboard";
            default -> "redirect:/dashboard";
        };
    }
}
