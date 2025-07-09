package com.school.service.impl;

import com.school.dto.ProfileEditRequestDTO;
import com.school.entity.ProfileEditRequest;
import com.school.entity.User;
import com.school.repository.ProfileEditRequestRepository;
import com.school.repository.BaseUserRepository;
import com.school.service.ProfileEditRequestService;
import com.school.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileEditRequestServiceImpl implements ProfileEditRequestService {

    private final ProfileEditRequestRepository requestRepository;
    private final BaseUserRepository userRepository;
    private final UserService userService;

    @Override
    public ProfileEditRequestDTO createRequest(ProfileEditRequestDTO requestDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Check if user already has a pending request
        if (hasUserPendingRequest(username)) {
            throw new RuntimeException("You already have a pending profile edit request. Please wait for admin approval.");
        }

        // Check if requested username is available
        if (!isUsernameAvailableForRequest(requestDTO.getRequestedUsername(), user.getUsername())) {
            throw new RuntimeException("The requested username is already taken or has a pending request.");
        }

        // Check if requested email is available
        if (!isEmailAvailableForRequest(requestDTO.getRequestedEmail(), user.getEmail())) {
            throw new RuntimeException("The requested email is already taken or has a pending request.");
        }

        ProfileEditRequest request = new ProfileEditRequest();
        request.setUser(user);
        request.setCurrentUsername(user.getUsername());
        request.setCurrentEmail(user.getEmail());
        request.setRequestedUsername(requestDTO.getRequestedUsername());
        request.setRequestedEmail(requestDTO.getRequestedEmail());
        request.setReason(requestDTO.getReason());
        request.setStatus(ProfileEditRequest.RequestStatus.PENDING);

        ProfileEditRequest savedRequest = requestRepository.save(request);
        log.info("Created profile edit request for user: {} with ID: {}", username, savedRequest.getId());

        return convertToDTO(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileEditRequestDTO getRequestById(Long id) {
        ProfileEditRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile edit request not found with ID: " + id));
        return convertToDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileEditRequestDTO> getRequestsByUserId(Long userId) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileEditRequestDTO> getRequestsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return requestRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(request -> request.getStatus() == ProfileEditRequest.RequestStatus.PENDING)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileEditRequestDTO> getAllRequests() {
        return requestRepository.findAllOrderByStatusAndCreatedAt()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileEditRequestDTO> getPendingRequests() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(ProfileEditRequest.RequestStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProfileEditRequestDTO approveRequest(Long id, String adminUsername, String adminComments) {
        ProfileEditRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile edit request not found with ID: " + id));

        if (request.getStatus() != ProfileEditRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found: " + adminUsername));

        // Apply the changes to the user profile
        User user = request.getUser();
        user.setUsername(request.getRequestedUsername());
        user.setEmail(request.getRequestedEmail());
        userRepository.save(user);

        // Update the request
        request.setStatus(ProfileEditRequest.RequestStatus.APPROVED);
        request.setApprovedBy(admin);
        request.setAdminComments(adminComments);
        request.setProcessedAt(LocalDateTime.now());

        ProfileEditRequest savedRequest = requestRepository.save(request);
        log.info("Approved profile edit request ID: {} by admin: {}", id, adminUsername);

        return convertToDTO(savedRequest);
    }

    @Override
    public ProfileEditRequestDTO rejectRequest(Long id, String adminUsername, String adminComments) {
        ProfileEditRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile edit request not found with ID: " + id));

        if (request.getStatus() != ProfileEditRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found: " + adminUsername));

        request.setStatus(ProfileEditRequest.RequestStatus.REJECTED);
        request.setApprovedBy(admin);
        request.setAdminComments(adminComments);
        request.setProcessedAt(LocalDateTime.now());

        ProfileEditRequest savedRequest = requestRepository.save(request);
        log.info("Rejected profile edit request ID: {} by admin: {}", id, adminUsername);

        return convertToDTO(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserPendingRequest(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return requestRepository.existsByUserAndStatus(user, ProfileEditRequest.RequestStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailableForRequest(String username, String currentUsername) {
        // Check if username is already taken by another user
        if (userService.existsByUsername(username) && !username.equals(currentUsername)) {
            return false;
        }
        
        // Check if there's a pending request for this username
        return requestRepository.findPendingRequestWithUsername(username, currentUsername).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailableForRequest(String email, String currentEmail) {
        // Check if email is already taken by another user
        if (userService.existsByEmail(email) && !email.equals(currentEmail)) {
            return false;
        }
        
        // Check if there's a pending request for this email
        return requestRepository.findPendingRequestWithEmail(email, currentEmail).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingRequestsCount() {
        return requestRepository.countByStatus(ProfileEditRequest.RequestStatus.PENDING);
    }

    @Override
    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new RuntimeException("Profile edit request not found with ID: " + id);
        }
        requestRepository.deleteById(id);
        log.info("Deleted profile edit request ID: {}", id);
    }

    private ProfileEditRequestDTO convertToDTO(ProfileEditRequest request) {
        ProfileEditRequestDTO dto = new ProfileEditRequestDTO();
        BeanUtils.copyProperties(request, dto);
        
        dto.setUserId(request.getUser().getId());
        dto.setUserName(request.getUser().getFirstName() + " " + request.getUser().getLastName());
        dto.setUserRole(request.getUser().getRole().name());
        
        if (request.getApprovedBy() != null) {
            dto.setApprovedById(request.getApprovedBy().getId());
            dto.setApprovedByName(request.getApprovedBy().getFirstName() + " " + request.getApprovedBy().getLastName());
        }
        
        return dto;
    }
}
