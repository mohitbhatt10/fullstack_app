package com.school.service;

import com.school.entity.FeatureSwitch;

import java.util.List;
import java.util.Optional;

public interface FeatureSwitchService {
    
    boolean isFeatureEnabled(String featureKey);
    
    void enableFeature(String featureKey, String updatedBy);
    
    void disableFeature(String featureKey, String updatedBy);
    
    Optional<FeatureSwitch> getFeatureSwitch(String featureKey);
    
    List<FeatureSwitch> getAllFeatureSwitches();
    
    FeatureSwitch createFeatureSwitch(String featureKey, String featureName, String description, Boolean enabled);
    
    void initializeDefaultFeatures();
}
