package store.pharmaquick.cart.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import store.pharmaquick.cart.dto.AddToCartRequest;
import store.pharmaquick.cart.dto.CartResponse;
import store.pharmaquick.cart.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    // ==========================================
    // ADD PRODUCT TO CART
    // ==========================================

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {

        String userId =
                authentication.getName();

        CartResponse response =
                cartService.addToCart(
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // GET MY CART
    // ==========================================

    @GetMapping
    public ResponseEntity<List<CartResponse>> getMyCart(
            Authentication authentication) {

        String userId =
                authentication.getName();

        return ResponseEntity.ok(
                cartService.getMyCart(userId)
        );
    }


    // ==========================================
    // UPDATE CART QUANTITY
    // ==========================================

    @PutMapping("/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {

        String userId =
                authentication.getName();

        CartResponse response =
                cartService.updateQuantity(
                        userId,
                        productId,
                        quantity
                );

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // REMOVE PRODUCT FROM CART
    // ==========================================

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromCart(
            @PathVariable Long productId,
            Authentication authentication) {

        String userId =
                authentication.getName();

        cartService.removeFromCart(
                userId,
                productId
        );

        return ResponseEntity.ok(
                "Product removed from cart successfully"
        );
    }


    // ==========================================
    // CLEAR CART
    // ==========================================

    @DeleteMapping
    public ResponseEntity<String> clearCart(
            Authentication authentication) {

        String userId =
                authentication.getName();

        cartService.clearCart(userId);

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}