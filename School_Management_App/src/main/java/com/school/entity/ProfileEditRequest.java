package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_edit_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_username")
    private String currentUsername;

    @Column(name = "current_email")
    private String currentEmail;

    @Column(name = "current_phone_number")
    private String currentPhoneNumber;

    @Column(name = "requested_username")
    private String requestedUsername;

    @Column(name = "requested_email")
    private String requestedEmail;

    @Column(name = "requested_phone_number")
    private String requestedPhoneNumber;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "admin_comments", columnDefinition = "TEXT")
    private String adminComments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
