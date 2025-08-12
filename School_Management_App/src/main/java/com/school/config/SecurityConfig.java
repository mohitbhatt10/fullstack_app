package com.school.config;

import com.school.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordConfig passwordConfig;
    private final TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler;

    public SecurityConfig(UserService userService, 
                         PasswordConfig passwordConfig,
                         TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler) {
        this.userService = userService;
        this.passwordConfig = passwordConfig;
        this.twoFactorAuthenticationSuccessHandler = twoFactorAuthenticationSuccessHandler;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/home","/forgot-password","/reset-password",
                                 "/about/admin","/about/teacher","/about/student").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/login-error").permitAll()
                .requestMatchers("/auth/2fa", "/auth/2fa/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .failureUrl("/login-error")
                .successHandler(twoFactorAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .sessionManagement((sessions) -> sessions
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false) // Changed to false to allow new logins
                .expiredUrl("/login?expired")
                .sessionRegistry(sessionRegistry())
            )
            .rememberMe((remember) -> remember
                .tokenValiditySeconds((int) Duration.ofDays(30).toSeconds())
                .key("uniqueAndSecret")
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
