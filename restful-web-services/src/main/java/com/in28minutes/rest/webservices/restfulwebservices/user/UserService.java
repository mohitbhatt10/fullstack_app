package com.in28minutes.rest.webservices.restfulwebservices.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        // Encode the password before saving it to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRoles() != null && !user.getRoles().isEmpty()) {
        	user.setRoles(new HashSet<>(roleRepository.findAllById(user.getRoles().stream().map(Role :: getRoleId).toList())));
        }else {
        	user.setRoles(new HashSet<>(List.of(roleRepository.findById(1L).get())));
        }
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        if (userRepository.existsById(userId)) {
        	User originalUser = userRepository.findById(userId).get();
            updatedUser.setUserId(userId);
            // Encode the password before updating it in the database
            if(updatedUser.getPassword() != null) {
            	updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            else {
            	updatedUser.setPassword(originalUser.getPassword());
            }
            return userRepository.save(updatedUser);
        } else {
            // Handle the case where the user with the given ID is not found
            return null;
        }
    }
    
    public User updateUserByUsername(String username, User updatedUser) {
    	Optional<User> OptionalOriginalUser = userRepository.findByUsername(username);
        if (OptionalOriginalUser.isPresent()) {
        	User originalUser = OptionalOriginalUser.get();
        	updatedUser.setUserId(originalUser.getUserId());
            // Encode the password before updating it in the database
            if(updatedUser.getPassword() != null) {
            	updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            else {
            	updatedUser.setPassword(originalUser.getPassword());
            }
            return userRepository.save(updatedUser);
        } else {
            // Handle the case where the user with the given ID is not found
            return null;
        }
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
