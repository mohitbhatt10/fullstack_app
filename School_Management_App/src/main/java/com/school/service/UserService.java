package com.school.service;

import com.school.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    com.school.entity.User findEntityByUsername(String username);
    void updateProfilePicture(String username, org.springframework.web.multipart.MultipartFile file);
    boolean changePassword(String username, String oldPassword, String newPassword);
}
