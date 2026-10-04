package store.pharmaquick.user.service;

import store.pharmaquick.user.entity.Role;
import store.pharmaquick.user.dto.SignupRequest;
import store.pharmaquick.user.entity.User;
import store.pharmaquick.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import store.pharmaquick.user.dto.UserResponse;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(SignupRequest request) {

        // Check User ID
        if (userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("User ID already exists");
        }

        // Check Email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check Mobile
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new RuntimeException("Mobile number already exists");
        }

        // Check Password
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Create User
        User user = new User();

        user.setUserId(request.getUserId());
        user.setName(request.getName());

        // Hash password before storing
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        // Every public signup creates a normal USER
        user.setRole(Role.USER);

        // Save user
        return userRepository.save(user);
    }

    public UserResponse getUserDetails(String userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getMobile()
        );
    }
}