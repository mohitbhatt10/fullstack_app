package com.school.service;

import com.school.exception.InvalidTokenException;
import com.school.model.PasswordResetToken;
import com.school.entity.User;
import com.school.repository.PasswordResetTokenRepository;
import com.school.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    @Qualifier("baseUserRepository")
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createPasswordResetTokenForUser(String email, String appUrl) {
        Optional userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }
        User user = (User) userOptional.get();
        // Delete existing token if present
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusHours(24));

        tokenRepository.save(myToken);

        String resetUrl = appUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    }

    @Transactional
    public User validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new InvalidTokenException("Token has expired");
        }

        return resetToken.getUser();
    }

    @Transactional
    public void changeUserPassword(User user, String newPassword, String appUrl) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove the token after successful password change
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Send notification email
        emailService.sendPasswordChangedNotification(user.getEmail(), appUrl);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteAllExpiredSince(LocalDateTime.now());
    }
}
