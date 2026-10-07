package store.pharmaquick.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.pharmaquick.order.entity.Order;
import store.pharmaquick.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    // Get all orders belonging to a user
    List<Order> findByUserOrderByCreatedAtDesc(
            User user
    );

    // Find a specific order belonging to a user
    Optional<Order> findByOrderIdAndUser(
            Long orderId,
            User user
    );
}