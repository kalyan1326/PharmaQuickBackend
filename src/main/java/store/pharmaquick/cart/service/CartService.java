package store.pharmaquick.cart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.pharmaquick.cart.dto.AddToCartRequest;
import store.pharmaquick.cart.dto.CartResponse;
import store.pharmaquick.cart.entity.Cart;
import store.pharmaquick.cart.repository.CartRepository;
import store.pharmaquick.exception.ResourceNotFoundException;
import store.pharmaquick.product.entity.Product;
import store.pharmaquick.product.repository.ProductRepository;
import store.pharmaquick.user.entity.User;
import store.pharmaquick.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    // ==========================================
    // ADD PRODUCT TO CART
    // ==========================================

    @Transactional
    public CartResponse addToCart(
            String userId,
            AddToCartRequest request) {

        User user = getUser(userId);

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        int requestedQuantity =
                request.getQuantity();

        // Check stock
        if (product.getStock() <= 0) {
            throw new RuntimeException(
                    "Product is out of stock"
            );
        }

        if (requestedQuantity > product.getStock()) {
            throw new RuntimeException(
                    "Requested quantity exceeds available stock"
            );
        }

        // Check if product already exists in cart
        Cart cart =
                cartRepository
                        .findByUserAndProduct(user, product)
                        .orElse(null);

        if (cart != null) {

            int newQuantity =
                    cart.getQuantity() + requestedQuantity;

            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Requested quantity exceeds available stock"
                );
            }

            cart.setQuantity(newQuantity);

        } else {

            cart = new Cart();

            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(requestedQuantity);

            // Use discount price if available
            BigDecimal price =
                    getEffectivePrice(product);

            cart.setPrice(price);
        }

        Cart savedCart =
                cartRepository.save(cart);

        return mapToResponse(savedCart);
    }


    // ==========================================
    // GET MY CART
    // ==========================================

    public List<CartResponse> getMyCart(
            String userId) {

        User user = getUser(userId);

        List<Cart> cartItems =
                cartRepository.findByUser(user);

        return cartItems.stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ==========================================
    // UPDATE CART QUANTITY
    // ==========================================

    @Transactional
    public CartResponse updateQuantity(
            String userId,
            Long productId,
            Integer quantity) {

        User user = getUser(userId);

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        Cart cart =
                cartRepository
                        .findByUserAndProduct(user, product)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product is not in your cart"
                                ));

        if (quantity == null || quantity < 1) {
            throw new RuntimeException(
                    "Quantity must be at least 1"
            );
        }

        if (quantity > product.getStock()) {
            throw new RuntimeException(
                    "Requested quantity exceeds available stock"
            );
        }

        cart.setQuantity(quantity);

        Cart updatedCart =
                cartRepository.save(cart);

        return mapToResponse(updatedCart);
    }


    // ==========================================
    // REMOVE PRODUCT FROM CART
    // ==========================================

    @Transactional
    public void removeFromCart(
            String userId,
            Long productId) {

        User user = getUser(userId);

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        Cart cart =
                cartRepository
                        .findByUserAndProduct(user, product)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product is not in your cart"
                                ));

        cartRepository.delete(cart);
    }


    // ==========================================
    // CLEAR CART
    // ==========================================

    @Transactional
    public void clearCart(
            String userId) {

        User user = getUser(userId);

        cartRepository.deleteByUser(user);
    }


    // ==========================================
    // GET USER
    // ==========================================

    private User getUser(String userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }


    // ==========================================
    // GET EFFECTIVE PRICE
    // ==========================================

    private BigDecimal getEffectivePrice(
            Product product) {

        if (product.getDiscountPrice() != null &&
                product.getDiscountPrice()
                        .compareTo(product.getPrice()) < 0) {

            return product.getDiscountPrice();
        }

        return product.getPrice();
    }


    // ==========================================
    // MAP CART → RESPONSE
    // ==========================================

    private CartResponse mapToResponse(
            Cart cart) {

        Product product =
                cart.getProduct();

        BigDecimal subtotal =
                cart.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cart.getQuantity()
                                )
                        );

        return new CartResponse(
                cart.getCartId(),
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getBrand(),
                cart.getPrice(),
                cart.getQuantity(),
                subtotal,
                product.getImageUrl(),
                product.getStock()
        );
    }
}