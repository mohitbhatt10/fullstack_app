package com.school.controller;

import com.school.entity.FeatureSwitch;
import com.school.service.FeatureSwitchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/features")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FeatureSwitchController {

    private final FeatureSwitchService featureSwitchService;

    @GetMapping
    public String listFeatureSwitches(Model model) {
        log.debug("Displaying feature switches management");
        List<FeatureSwitch> features = featureSwitchService.getAllFeatureSwitches();
        model.addAttribute("features", features);
        return "admin/features/list";
    }

    @PostMapping("/{featureKey}/toggle")
    @ResponseBody
    public ResponseEntity<String> toggleFeature(@PathVariable String featureKey,
                                               Principal principal) {
        try {
            FeatureSwitch feature = featureSwitchService.getFeatureSwitch(featureKey)
                .orElseThrow(() -> new RuntimeException("Feature not found"));

            if (feature.getEnabled()) {
                featureSwitchService.disableFeature(featureKey, principal.getName());
                log.info("Feature {} disabled by user: {}", featureKey, principal.getName());
                return ResponseEntity.ok("Feature disabled successfully");
            } else {
                featureSwitchService.enableFeature(featureKey, principal.getName());
                log.info("Feature {} enabled by user: {}", featureKey, principal.getName());
                return ResponseEntity.ok("Feature enabled successfully");
            }
        } catch (Exception e) {
            log.error("Error toggling feature: {}", featureKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to toggle feature: " + e.getMessage());
        }
    }

    @GetMapping("/2fa-status")
    @ResponseBody
    public boolean get2FAStatus() {
        return featureSwitchService.isFeatureEnabled("TWO_FACTOR_AUTH");
    }
}
