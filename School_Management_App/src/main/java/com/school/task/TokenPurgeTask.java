package com.school.task;

import com.school.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenPurgeTask {

    @Autowired
    private PasswordResetService passwordResetService;

    // Run once a day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void purgeExpiredTokens() {
        passwordResetService.cleanupExpiredTokens();
    }
}
