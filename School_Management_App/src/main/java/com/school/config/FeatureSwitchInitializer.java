package com.school.config;

import com.school.service.FeatureSwitchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "startup_entities_required",
        havingValue = "true"
)
public class FeatureSwitchInitializer implements CommandLineRunner {

    private final FeatureSwitchService featureSwitchService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing feature switches...");
        featureSwitchService.initializeDefaultFeatures();
        log.info("Feature switches initialization completed");
    }
}
