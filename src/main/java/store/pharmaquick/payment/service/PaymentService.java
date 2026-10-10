
package store.pharmaquick.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.pharmaquick.cart.repository.CartRepository;

import store.pharmaquick.order.entity.Order;
import store.pharmaquick.order.entity.OrderItem;
import store.pharmaquick.order.entity.OrderStatus;
import store.pharmaquick.order.repository.OrderItemRepository;
import store.pharmaquick.order.repository.OrderRepository;

import store.pharmaquick.payment.dto.CreatePaymentRequest;
import store.pharmaquick.payment.dto.PaymentOrderResponse;
import store.pharmaquick.payment.dto.VerifyPaymentRequest;
import store.pharmaquick.payment.entity.Payment;
import store.pharmaquick.payment.entity.PaymentStatus;
import store.pharmaquick.payment.repository.PaymentRepository;

import store.pharmaquick.product.entity.Product;
import store.pharmaquick.product.repository.ProductRepository;

import store.pharmaquick.user.entity.User;
import store.pharmaquick.user.repository.UserRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            RazorpayService razorpayService) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.razorpayService = razorpayService;
    }

    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @Transactional
    public PaymentOrderResponse createPaymentOrder(
            String userId,
            CreatePaymentRequest request) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Order order = orderRepository
                .findByOrderIdAndUser(request.getOrderId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Cannot make payment for a cancelled order");
        }

        if (order.getPaymentStatus()
                == store.pharmaquick.order.entity.PaymentStatus.PAID) {
            throw new RuntimeException("Order is already paid");
        }

        Payment existingPayment = paymentRepository
                .findByOrder(order)
                .orElse(null);

        // Reuse an existing Razorpay order when possible.
        if (existingPayment != null
                && existingPayment.getStatus() == PaymentStatus.CREATED) {

            return buildResponse(order, existingPayment);
        }

        BigDecimal amount = order.getTotalAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid order amount");
        }

        String receipt = "ORDER_" + order.getOrderId();

        com.razorpay.Order razorpayOrder =
                razorpayService.createRazorpayOrder(
                        amount,
                        receipt);

        String razorpayOrderId = razorpayOrder.get("id");

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setStatus(PaymentStatus.CREATED);

        Payment savedPayment = paymentRepository.save(payment);

        return buildResponse(order, savedPayment);
    }

    // =========================================================
    // VERIFY PAYMENT
    // =========================================================

    @Transactional
    public PaymentOrderResponse verifyPayment(
            String userId,
            VerifyPaymentRequest request) {

        // 1. Find the logged-in user.
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Find the order belonging to this user.
        Order order = orderRepository
                .findByOrderIdAndUser(request.getOrderId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        // 3. Find the payment associated with this order.
        Payment payment = paymentRepository
                .findByOrder(order)
                .orElseThrow(() ->
                        new RuntimeException("Payment record not found"));

        // 4. Verify the Razorpay order ID.
        if (!Objects.equals(
                payment.getRazorpayOrderId(),
                request.getRazorpayOrderId())) {

            throw new IllegalArgumentException(
                    "Razorpay order ID does not match");
        }

        // 5. Handle repeated verification of the same payment.
        // Do not deduct stock or clear the cart a second time.
        if (payment.getStatus() == PaymentStatus.SUCCESS) {

            boolean samePayment =
                    Objects.equals(
                            payment.getRazorpayPaymentId(),
                            request.getRazorpayPaymentId())
                            && Objects.equals(
                            payment.getRazorpaySignature(),
                            request.getRazorpaySignature());

            if (!samePayment) {
                throw new IllegalArgumentException(
                        "This order has already been paid");
            }

            return buildResponse(order, payment);
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Cannot verify payment for a cancelled order");
        }

        // 6. Verify the Razorpay signature before changing data.
        boolean signatureValid =
                razorpayService.verifyPaymentSignature(
                        request.getRazorpayOrderId(),
                        request.getRazorpayPaymentId(),
                        request.getRazorpaySignature());

        if (!signatureValid) {
            throw new IllegalArgumentException(
                    "Invalid Razorpay payment signature");
        }

        // 7. Load the order items.
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        if (orderItems.isEmpty()) {
            throw new RuntimeException(
                    "Cannot complete payment for an order without items");
        }

        // 8. Validate stock for every item before deducting any stock.
        for (OrderItem item : orderItems) {

            Product product = item.getProduct();

            if (product == null
                    || product.getStock() == null
                    || item.getQuantity() == null
                    || item.getQuantity() <= 0) {

                throw new RuntimeException(
                        "Invalid product or quantity in order");
            }

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + item.getProductName()
                                + ". The successful payment requires reconciliation.");
            }
        }

        // 9. Deduct stock after successful signature verification.
        for (OrderItem item : orderItems) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock() - item.getQuantity());

            productRepository.save(product);
        }

        // 10. Save verified Razorpay payment details.
        payment.setRazorpayPaymentId(
                request.getRazorpayPaymentId());

        payment.setRazorpaySignature(
                request.getRazorpaySignature());

        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        // 11. Mark the PharmaQuick order as paid and confirmed.
        order.setPaymentStatus(
                store.pharmaquick.order.entity.PaymentStatus.PAID);

        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);

        // 12. Clear the cart only after successful payment verification.
        cartRepository.deleteByUser(user);

        return buildResponse(order, payment);
    }

    // =========================================================
    // BUILD PAYMENT RESPONSE
    // =========================================================

    private PaymentOrderResponse buildResponse(
            Order order,
            Payment payment) {

        return new PaymentOrderResponse(
                order.getOrderId(),
                payment.getPaymentId(),
                payment.getRazorpayOrderId(),
                razorpayKeyId,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name());
    }
}
