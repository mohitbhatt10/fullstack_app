package com.school.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feature_switches")
public class FeatureSwitch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Feature key is required")
    @Column(nullable = false, unique = true)
    private String featureKey;

    @NotBlank(message = "Feature name is required")
    @Column(nullable = false)
    private String featureName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructor for creating feature switches
    public FeatureSwitch(String featureKey, String featureName, String description, Boolean enabled) {
        this.featureKey = featureKey;
        this.featureName = featureName;
        this.description = description;
        this.enabled = enabled;
    }

    public FeatureSwitch() {}
}
