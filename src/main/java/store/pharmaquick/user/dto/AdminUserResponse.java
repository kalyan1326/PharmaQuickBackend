
package store.pharmaquick.user.dto;

import store.pharmaquick.user.entity.Role;

public record AdminUserResponse(
        String userId,
        String name,
        String email,
        String mobile,
        Role role
) {
}
