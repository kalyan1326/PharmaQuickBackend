package store.pharmaquick.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.pharmaquick.cart.entity.Cart;
import store.pharmaquick.product.entity.Product;
import store.pharmaquick.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Get all cart items belonging to a user
    List<Cart> findByUser(User user);

    // Find a particular product inside a user's cart
    Optional<Cart> findByUserAndProduct(
            User user,
            Product product
    );

    // Delete a particular product from user's cart
    void deleteByUserAndProduct(
            User user,
            Product product
    );

    // Delete all cart items belonging to a user
    void deleteByUser(User user);
}