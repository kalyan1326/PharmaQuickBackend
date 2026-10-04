package store.pharmaquick.auth.service;

import store.pharmaquick.auth.dto.LoginRequest;
import store.pharmaquick.auth.entity.JwtAuthToken;
import store.pharmaquick.user.entity.Role;
import store.pharmaquick.user.entity.User;
import store.pharmaquick.auth.repository.JwtTokenRepository;
import store.pharmaquick.user.repository.UserRepository;
import store.pharmaquick.auth.security.JwtService;
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

    // ==============================
    // USER LOGIN
    // ==============================

    public String login(LoginRequest request) {

        // Find user using userId
        User user = userRepository
                .findById(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        // Only USER can use normal login
        if (user.getRole() != Role.USER) {
            throw new RuntimeException("Invalid username or password");
        }

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


    // ==============================
    // ADMIN LOGIN
    // ==============================

    public String adminLogin(LoginRequest request) {

        // Find user using userId
        User user = userRepository
                .findById(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid admin credentials"));

        // Only ADMIN can use admin login
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Invalid admin credentials");
        }

        // Verify password
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid admin credentials");
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


    // ==============================
    // LOGOUT
    // ==============================

    @Transactional
    public void logout(String token) {

        if (token != null && !token.isBlank()) {
            jwtTokenRepository.deleteByToken(token);
        }
    }
}