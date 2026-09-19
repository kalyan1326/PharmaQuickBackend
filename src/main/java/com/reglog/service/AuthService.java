package com.reglog.service;

import com.reglog.dto.LoginRequest;
import com.reglog.entity.JwtAuthToken;
import com.reglog.entity.User;
import com.reglog.repository.JwtTokenRepository;
import com.reglog.repository.UserRepository;
import com.reglog.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            JwtTokenRepository jwtTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {

        // Find user using userId
        User user = userRepository
                .findById(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        // Verify password
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT
        String token = jwtService.generateToken(user.getUserId());

        // Token creation time
        LocalDateTime createdAt = LocalDateTime.now();

        // Token expiration time
        LocalDateTime expiresAt = createdAt.plusHours(1);

        // Create JWT entity
        JwtAuthToken jwtAuthToken = new JwtAuthToken();

        jwtAuthToken.setUser(user);
        jwtAuthToken.setToken(token);
        jwtAuthToken.setCreatedAt(createdAt);
        jwtAuthToken.setExpiresAt(expiresAt);

        // Save JWT in database
        jwtTokenRepository.save(jwtAuthToken);

        return token;
    }

    @Transactional
    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            jwtTokenRepository.deleteByToken(token);
        }
    }
}