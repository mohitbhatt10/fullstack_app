package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private static final List<String> DEFAULT_ROLES = List.of(
            "ROLE_USER",
            "ROLE_ADMIN",
            "ROLE_MODERATOR");

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        DEFAULT_ROLES.stream()
                .filter(roleName -> !roleRepository.existsByRoleName(roleName))
                .map(Role::new)
                .forEach(role -> {
                    roleRepository.save(role);
                    LOGGER.info("Seeded role: {}", role.getRoleName());
                });
    }
}
