package com.school.service.impl;

import com.school.entity.FeatureSwitch;
import com.school.repository.FeatureSwitchRepository;
import com.school.service.FeatureSwitchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureSwitchServiceImpl implements FeatureSwitchService {

    private final FeatureSwitchRepository featureSwitchRepository;

    // Feature constants
    public static final String TWO_FACTOR_AUTH = "TWO_FACTOR_AUTH";
    public static final String EMAIL_NOTIFICATIONS = "EMAIL_NOTIFICATIONS";
    public static final String SMS_NOTIFICATIONS = "SMS_NOTIFICATIONS";

    @Override
    public boolean isFeatureEnabled(String featureKey) {
        try {
            return featureSwitchRepository.isFeatureEnabled(featureKey).orElse(false);
        } catch (Exception e) {
            log.error("Error checking feature switch for key: {}", featureKey, e);
            return false; // Default to disabled if there's an error
        }
    }

    @Override
    @Transactional
    public void enableFeature(String featureKey, String updatedBy) {
        log.info("Enabling feature: {} by user: {}", featureKey, updatedBy);
        Optional<FeatureSwitch> featureOpt = featureSwitchRepository.findByFeatureKey(featureKey);
        
        if (featureOpt.isPresent()) {
            FeatureSwitch feature = featureOpt.get();
            feature.setEnabled(true);
            feature.setUpdatedBy(updatedBy);
            featureSwitchRepository.save(feature);
            log.info("Feature {} enabled successfully", featureKey);
        } else {
            log.warn("Feature switch not found for key: {}", featureKey);
            throw new RuntimeException("Feature switch not found: " + featureKey);
        }
    }

    @Override
    @Transactional
    public void disableFeature(String featureKey, String updatedBy) {
        log.info("Disabling feature: {} by user: {}", featureKey, updatedBy);
        Optional<FeatureSwitch> featureOpt = featureSwitchRepository.findByFeatureKey(featureKey);
        
        if (featureOpt.isPresent()) {
            FeatureSwitch feature = featureOpt.get();
            feature.setEnabled(false);
            feature.setUpdatedBy(updatedBy);
            featureSwitchRepository.save(feature);
            log.info("Feature {} disabled successfully", featureKey);
        } else {
            log.warn("Feature switch not found for key: {}", featureKey);
            throw new RuntimeException("Feature switch not found: " + featureKey);
        }
    }

    @Override
    public Optional<FeatureSwitch> getFeatureSwitch(String featureKey) {
        return featureSwitchRepository.findByFeatureKey(featureKey);
    }

    @Override
    public List<FeatureSwitch> getAllFeatureSwitches() {
        return featureSwitchRepository.findAll();
    }

    @Override
    @Transactional
    public FeatureSwitch createFeatureSwitch(String featureKey, String featureName, String description, Boolean enabled) {
        log.info("Creating feature switch: {}", featureKey);
        
        if (featureSwitchRepository.existsByFeatureKey(featureKey)) {
            throw new RuntimeException("Feature switch already exists: " + featureKey);
        }
        
        FeatureSwitch featureSwitch = new FeatureSwitch(featureKey, featureName, description, enabled);
        return featureSwitchRepository.save(featureSwitch);
    }

    @Override
    @Transactional
    public void initializeDefaultFeatures() {
        log.info("Initializing default feature switches");
        
        // Initialize Two-Factor Authentication feature
        if (!featureSwitchRepository.existsByFeatureKey(TWO_FACTOR_AUTH)) {
            createFeatureSwitch(
                TWO_FACTOR_AUTH,
                "Two-Factor Authentication",
                "Enable SMS-based two-factor authentication for enhanced security",
                false // Initially disabled
            );
        }
        
        // Initialize Email Notifications feature
        if (!featureSwitchRepository.existsByFeatureKey(EMAIL_NOTIFICATIONS)) {
            createFeatureSwitch(
                EMAIL_NOTIFICATIONS,
                "Email Notifications",
                "Enable email notifications for system events",
                true // Initially enabled
            );
        }
        
        // Initialize SMS Notifications feature
        if (!featureSwitchRepository.existsByFeatureKey(SMS_NOTIFICATIONS)) {
            createFeatureSwitch(
                SMS_NOTIFICATIONS,
                "SMS Notifications",
                "Enable SMS notifications for system events",
                false // Initially disabled
            );
        }
        
        log.info("Default feature switches initialized");
    }
}
