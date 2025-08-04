package com.school.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {
    
    private Account account = new Account();
    private Auth auth = new Auth();
    private Phone phone = new Phone();
    private boolean enabled = false;
    
    @Data
    public static class Account {
        private String sid;
    }
    
    @Data
    public static class Auth {
        private String token;
    }
    
    @Data
    public static class Phone {
        private String number;
    }
}
