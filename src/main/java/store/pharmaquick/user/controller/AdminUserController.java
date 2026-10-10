
package store.pharmaquick.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import store.pharmaquick.user.dto.AdminUserResponse;
import store.pharmaquick.user.service.AdminUserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // GET ALL USERS
    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }

    // GET USER BY ID
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                adminUserService.getUserById(userId)
        );
    }
}
