package com.school.repository;

import com.school.entity.ProfileEditRequest;
import com.school.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileEditRequestRepository extends JpaRepository<ProfileEditRequest, Long> {

    List<ProfileEditRequest> findByUserOrderByCreatedAtDesc(User user);

    List<ProfileEditRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ProfileEditRequest> findByStatusOrderByCreatedAtDesc(ProfileEditRequest.RequestStatus status);

    @Query("SELECT per FROM ProfileEditRequest per ORDER BY " +
           "CASE WHEN per.status = 'PENDING' THEN 1 " +
           "WHEN per.status = 'APPROVED' THEN 2 " +
           "WHEN per.status = 'REJECTED' THEN 3 END, " +
           "per.createdAt DESC")
    List<ProfileEditRequest> findAllOrderByStatusAndCreatedAt();

    Optional<ProfileEditRequest> findByUserAndStatus(User user, ProfileEditRequest.RequestStatus status);

    @Query("SELECT COUNT(per) FROM ProfileEditRequest per WHERE per.status = :status")
    long countByStatus(@Param("status") ProfileEditRequest.RequestStatus status);

    boolean existsByUserAndStatus(User user, ProfileEditRequest.RequestStatus status);

    @Query("SELECT per FROM ProfileEditRequest per WHERE " +
           "per.requestedUsername = :username AND per.status = 'PENDING' AND per.user.username != :currentUsername")
    Optional<ProfileEditRequest> findPendingRequestWithUsername(@Param("username") String username, 
                                                               @Param("currentUsername") String currentUsername);

    @Query("SELECT per FROM ProfileEditRequest per WHERE " +
           "per.requestedEmail = :email AND per.status = 'PENDING' AND per.user.email != :currentEmail")
    Optional<ProfileEditRequest> findPendingRequestWithEmail(@Param("email") String email, 
                                                            @Param("currentEmail") String currentEmail);
}
