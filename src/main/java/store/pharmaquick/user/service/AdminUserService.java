
package store.pharmaquick.user.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.pharmaquick.exception.ResourceNotFoundException;
import store.pharmaquick.user.dto.AdminUserResponse;
import store.pharmaquick.user.entity.User;
import store.pharmaquick.user.repository.UserRepository;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users, sorted by name
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {

        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get one user by user ID
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        return mapToResponse(user);
    }

    private AdminUserResponse mapToResponse(User user) {

        return new AdminUserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getMobile(),
                user.getRole()
        );
    }
}
