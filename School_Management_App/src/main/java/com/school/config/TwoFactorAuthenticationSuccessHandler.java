package com.school.config;

import com.school.entity.User;
import com.school.service.TwoFactorAuthService;
import com.school.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        log.info("Authentication successful for user: {}", username);

        try {
            User user = userService.findEntityByUsername(username);
            
            // Check if 2FA is enabled and user is eligible
            if (twoFactorAuthService.isUserEligibleFor2FA(user)) {
                log.info("2FA is enabled for user: {}, redirecting to 2FA page", username);
                
                // Store username in session for 2FA process
                HttpSession session = request.getSession();
                session.setAttribute("pre_auth_username", username);
                
                // Invalidate the current authentication (user is not fully authenticated yet)
                request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", null);
                
                response.sendRedirect("/auth/2fa");
                return;
            }

            // If 2FA is not enabled or user is not eligible, proceed with normal login
            log.info("2FA not required for user: {}, proceeding with normal login", username);
            String redirectUrl = getRedirectUrlBasedOnRole(authentication);
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            log.error("Error during authentication success handling for user: {}", username, e);
            response.sendRedirect("/login?error=auth_processing_failed");
        }
    }

    private String getRedirectUrlBasedOnRole(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            return "/teacher/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            return "/student/dashboard";
        }
        return "/dashboard";
    }
}
