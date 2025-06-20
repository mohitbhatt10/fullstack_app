package com.school.controller;

import com.school.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfilePictureController {

    private final UserService userService;

    public ProfilePictureController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        var user = userService.getUserById(userId);

        if (user == null || user.getProfilePicture() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(user.getProfilePictureContentType() == null ? MediaType.MULTIPART_FORM_DATA_VALUE : user.getProfilePictureContentType()));
        headers.setContentLength(user.getProfilePicture().length);

        return new ResponseEntity<>(user.getProfilePicture(), headers, HttpStatus.OK);
    }
}
