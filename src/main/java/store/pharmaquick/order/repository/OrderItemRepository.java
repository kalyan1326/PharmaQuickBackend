package store.pharmaquick.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.pharmaquick.order.entity.Order;
import store.pharmaquick.order.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // Get all items belonging to an order
    List<OrderItem> findByOrder(Order order);
}