package store.pharmaquick.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.pharmaquick.order.entity.Order;
import store.pharmaquick.order.entity.ShippingAddress;

import java.util.Optional;

public interface ShippingAddressRepository
        extends JpaRepository<ShippingAddress, Long> {

    // Get shipping address for an order
    Optional<ShippingAddress> findByOrder(
            Order order
    );
}