package com.school.service.impl;

import com.school.dto.UserDTO;
import com.school.entity.User;
import com.school.repository.BaseUserRepository;
import com.school.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final BaseUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(BaseUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO savedDTO = new UserDTO();
        BeanUtils.copyProperties(savedUser, savedDTO);
        return savedDTO;
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(userDTO, user, "id", "password");
        User updatedUser = userRepository.save(user);
        UserDTO updatedDTO = new UserDTO();
        BeanUtils.copyProperties(updatedUser, updatedDTO);
        return updatedDTO;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public User findEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void updateProfilePicture(String username, org.springframework.web.multipart.MultipartFile file) {
        try {
            User user = findEntityByUsername(username);
            // Save file to /static/images/profile/ or another location, set URL
            /*String fileName = "profile-" + user.getId() + "-" + System.currentTimeMillis() + ".jpg";
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("src/main/resources/static/images/profile");
            java.nio.file.Files.createDirectories(uploadPath);
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath);
            */
            user.setProfilePicture(file.getBytes());
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = findEntityByUsername(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
