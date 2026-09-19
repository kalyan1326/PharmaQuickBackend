package com.reglog.controller;

import com.reglog.dto.SignupRequest;
import com.reglog.entity.User;
import com.reglog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.reglog.dto.UserResponse;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(Authentication authentication) {

        try {

            String userId = authentication.getName();

            UserResponse userResponse =
                    userService.getUserDetails(userId);

            return ResponseEntity.ok(userResponse);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest request) {

        try {

            User user = userService.signup(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully");

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}