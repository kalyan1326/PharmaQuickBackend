package store.pharmaquick.order.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import store.pharmaquick.order.dto.CreateOrderRequest;
import store.pharmaquick.order.dto.OrderResponse;
import store.pharmaquick.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // ==========================================
    // CREATE ORDER
    // ==========================================

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        String userId = authentication.getName();

        OrderResponse response =
                orderService.createOrder(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ==========================================
    // GET MY ORDERS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                orderService.getMyOrders(userId)
        );
    }


    // ==========================================
    // GET MY ORDER BY ID
    // ==========================================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                orderService.getMyOrder(
                        userId,
                        orderId
                )
        );
    }
}