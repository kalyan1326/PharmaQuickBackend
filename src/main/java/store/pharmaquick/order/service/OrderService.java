package store.pharmaquick.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.pharmaquick.cart.entity.Cart;
import store.pharmaquick.cart.repository.CartRepository;

import store.pharmaquick.exception.ResourceNotFoundException;

import store.pharmaquick.order.dto.CreateOrderRequest;
import store.pharmaquick.order.dto.OrderItemResponse;
import store.pharmaquick.order.dto.OrderResponse;
import store.pharmaquick.order.dto.ShippingAddressRequest;
import store.pharmaquick.order.dto.ShippingAddressResponse;

import store.pharmaquick.order.entity.Order;
import store.pharmaquick.order.entity.OrderItem;
import store.pharmaquick.order.entity.OrderStatus;
import store.pharmaquick.order.entity.PaymentStatus;
import store.pharmaquick.order.entity.ShippingAddress;

import store.pharmaquick.order.repository.OrderItemRepository;
import store.pharmaquick.order.repository.OrderRepository;
import store.pharmaquick.order.repository.ShippingAddressRepository;

import store.pharmaquick.product.entity.Product;
import store.pharmaquick.product.repository.ProductRepository;

import store.pharmaquick.user.entity.User;
import store.pharmaquick.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ShippingAddressRepository shippingAddressRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    // ==========================================
    // CREATE ORDER
    // ==========================================

    @Transactional
    public OrderResponse createOrder(
            String userId,
            CreateOrderRequest request) {

        // 1. Get logged-in user
        User user = getUser(userId);

        // 2. Get user's cart
        List<Cart> cartItems =
                cartRepository.findByUser(user);

        // 3. Cart must not be empty
        if (cartItems.isEmpty()) {
            throw new RuntimeException(
                    "Cannot place order because cart is empty"
            );
        }

        // 4. Create order
        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems =
                new ArrayList<>();


        // ==========================================
        // PROCESS CART ITEMS
        // ==========================================

        for (Cart cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            // Re-check stock during checkout
            if (product.getStock() <= 0) {

                throw new RuntimeException(
                        "Product is out of stock: "
                                + product.getName()
                );
            }

            if (cartItem.getQuantity()
                    > product.getStock()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }


            // Price from cart snapshot
            BigDecimal price =
                    cartItem.getPrice();

            Integer quantity =
                    cartItem.getQuantity();


            // Calculate subtotal
            BigDecimal subtotal =
                    price.multiply(
                            BigDecimal.valueOf(quantity)
                    );


            // Create order item
            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);

            // Snapshot product information
            orderItem.setProductName(
                    product.getName()
            );

            orderItem.setPrice(price);
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(subtotal);


            orderItems.add(orderItem);

            totalAmount =
                    totalAmount.add(subtotal);
        }


        // ==========================================
        // SAVE ORDER
        // ==========================================

        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);


        // ==========================================
        // SAVE ORDER ITEMS
        // ==========================================

        for (OrderItem orderItem : orderItems) {

            orderItem.setOrder(savedOrder);

            orderItemRepository.save(orderItem);
        }


        // ==========================================
        // SAVE SHIPPING ADDRESS
        // ==========================================

        ShippingAddressRequest addressRequest =
                request.getShippingAddress();

        ShippingAddress shippingAddress =
                new ShippingAddress();

        shippingAddress.setOrder(savedOrder);

        shippingAddress.setFullName(
                addressRequest.getFullName()
        );

        shippingAddress.setMobile(
                addressRequest.getMobile()
        );

        shippingAddress.setAddressLine(
                addressRequest.getAddressLine()
        );

        shippingAddress.setCity(
                addressRequest.getCity()
        );

        shippingAddress.setState(
                addressRequest.getState()
        );

        shippingAddress.setPincode(
                addressRequest.getPincode()
        );

        shippingAddressRepository.save(
                shippingAddress
        );


        // ==========================================
        // REDUCE PRODUCT STOCK
        // ==========================================

        for (Cart cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            int remainingStock =
                    product.getStock()
                            - cartItem.getQuantity();

            product.setStock(
                    remainingStock
            );

            productRepository.save(product);
        }


        // ==========================================
        // CLEAR CART
        // ==========================================

        cartRepository.deleteByUser(user);


        // ==========================================
        // RETURN ORDER
        // ==========================================

        return mapToResponse(
                savedOrder,
                orderItems,
                shippingAddress
        );
    }


    // ==========================================
    // GET MY ORDERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(
            String userId) {

        User user = getUser(userId);

        List<Order> orders =
                orderRepository
                        .findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::mapOrderToResponse)
                .toList();
    }


    // ==========================================
    // GET MY ORDER BY ID
    // ==========================================

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(
            String userId,
            Long orderId) {

        User user = getUser(userId);

        Order order =
                orderRepository
                        .findByOrderIdAndUser(
                                orderId,
                                user
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                ));

        return mapOrderToResponse(order);
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
    // MAP ORDER TO RESPONSE
    // ==========================================

    private OrderResponse mapOrderToResponse(
            Order order) {

        List<OrderItem> orderItems =
                orderItemRepository
                        .findByOrder(order);

        ShippingAddress shippingAddress =
                shippingAddressRepository
                        .findByOrder(order)
                        .orElse(null);

        return mapToResponse(
                order,
                orderItems,
                shippingAddress
        );
    }


    // ==========================================
    // MAP RESPONSE
    // ==========================================

    private OrderResponse mapToResponse(
            Order order,
            List<OrderItem> orderItems,
            ShippingAddress shippingAddress) {

        List<OrderItemResponse> itemResponses =
                orderItems.stream()
                        .map(this::mapItemToResponse)
                        .toList();


        ShippingAddressResponse
                shippingAddressResponse = null;

        if (shippingAddress != null) {

            shippingAddressResponse =
                    new ShippingAddressResponse(
                            shippingAddress.getAddressId(),
                            shippingAddress.getFullName(),
                            shippingAddress.getMobile(),
                            shippingAddress.getAddressLine(),
                            shippingAddress.getCity(),
                            shippingAddress.getState(),
                            shippingAddress.getPincode()
                    );
        }


        return new OrderResponse(
                order.getOrderId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                shippingAddressResponse,
                itemResponses
        );
    }


    // ==========================================
    // MAP ORDER ITEM
    // ==========================================

    private OrderItemResponse mapItemToResponse(
            OrderItem orderItem) {

        Product product =
                orderItem.getProduct();

        return new OrderItemResponse(
                orderItem.getOrderItemId(),
                product.getProductId(),
                orderItem.getProductName(),
                product.getImageUrl(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal()
        );
    }
}