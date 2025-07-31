package com.school.repository;

import com.school.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByPhoneNumberAndOtpCodeAndVerifiedFalse(String phoneNumber, String otpCode);

    Optional<OtpVerification> findBySessionTokenAndVerifiedTrue(String sessionToken);

    @Query("SELECT o FROM OtpVerification o WHERE o.phoneNumber = :phoneNumber AND o.verified = false AND o.expiresAt > :now ORDER BY o.createdAt DESC")
    Optional<OtpVerification> findLatestUnverifiedOtp(@Param("phoneNumber") String phoneNumber, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE OtpVerification o SET o.verified = true, o.verifiedAt = :verifiedAt WHERE o.id = :id")
    void markAsVerified(@Param("id") Long id, @Param("verifiedAt") LocalDateTime verifiedAt);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :expiredTime")
    void deleteExpiredOtps(@Param("expiredTime") LocalDateTime expiredTime);

    @Modifying
    @Transactional
    @Query("UPDATE OtpVerification o SET o.attemptCount = o.attemptCount + 1 WHERE o.id = :id")
    void incrementAttemptCount(@Param("id") Long id);
}
