package com.in28minutes.rest.webservices.restfulwebservices.user;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if(user.isPresent()) {
        	User originalUser = user.get();
        	originalUser.setRoles(new HashSet<>(roleRepository.findAllById(originalUser.getRoles().stream().map(Role :: getRoleId).toList())));
        	return ResponseEntity.ok(originalUser);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if(user.isPresent()) {
        	User originalUser = user.get();
        	originalUser.setRoles(new HashSet<>(roleRepository.findAllById(originalUser.getRoles().stream().map(Role :: getRoleId).toList())));
        	return ResponseEntity.ok(originalUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            Optional<User> existingUser = userService.getUserByUsername(user.getUsername());
            if (existingUser.isPresent()) {
                // Username is already taken
                return createErrorResponse("Username is already taken, Please try with different username", "DUPLICATE_USERNAME");
            }

            existingUser = userService.getUserByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                // Email is already registered
                return createErrorResponse("Email is already registered, Please try with different username", "DUPLICATE_EMAIL");
            }

            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(
                    new CustomizedResponseWithMessage("User Created Successfully with id: "+createdUser.getUserId()), null, HttpStatus.CREATED);
        } catch (Exception e) {
            return createErrorResponse("Unexpected error occurred", "INTERNAL_ERROR");
        }
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    
    @PutMapping(value = "username/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUserByUsername(@PathVariable String username, @RequestBody User updatedUser) {
        User user = userService.updateUserByUsername(username, updatedUser);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(
                new CustomizedResponseWithMessage("User Deleted Successfully with id: "+userId), null, HttpStatus.OK);
    }
    
    private ResponseEntity<CustomErrorResponse> createErrorResponse(String errorMessage, String errorCode) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setErrorMessage(errorMessage);
        errorResponse.setErrorCode(errorCode);
        errorResponse.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}