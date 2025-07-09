package com.school.service;

import com.school.dto.ProfileEditRequestDTO;

import java.util.List;

public interface ProfileEditRequestService {

    ProfileEditRequestDTO createRequest(ProfileEditRequestDTO requestDTO, String username);

    ProfileEditRequestDTO getRequestById(Long id);

    List<ProfileEditRequestDTO> getRequestsByUserId(Long userId);

    List<ProfileEditRequestDTO> getRequestsByUsername(String username);

    List<ProfileEditRequestDTO> getAllRequests();

    List<ProfileEditRequestDTO> getPendingRequests();

    ProfileEditRequestDTO approveRequest(Long id, String adminUsername, String adminComments);

    ProfileEditRequestDTO rejectRequest(Long id, String adminUsername, String adminComments);

    boolean hasUserPendingRequest(String username);

    boolean isUsernameAvailableForRequest(String username, String currentUsername);

    boolean isEmailAvailableForRequest(String email, String currentEmail);

    long getPendingRequestsCount();

    void deleteRequest(Long id);
}
