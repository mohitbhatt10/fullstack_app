package com.school.repository;

import com.school.entity.FeatureSwitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureSwitchRepository extends JpaRepository<FeatureSwitch, Long> {

    Optional<FeatureSwitch> findByFeatureKey(String featureKey);

    @Query("SELECT f.enabled FROM FeatureSwitch f WHERE f.featureKey = :featureKey")
    Optional<Boolean> isFeatureEnabled(@Param("featureKey") String featureKey);

    boolean existsByFeatureKey(String featureKey);
}
