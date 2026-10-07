package store.pharmaquick.order.dto;

import store.pharmaquick.order.entity.OrderStatus;
import store.pharmaquick.order.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ShippingAddressResponse shippingAddress;

    private List<OrderItemResponse> items;


    // =========================
    // CONSTRUCTOR
    // =========================

    public OrderResponse() {
    }

    public OrderResponse(
            Long orderId,
            BigDecimal totalAmount,
            OrderStatus status,
            PaymentStatus paymentStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ShippingAddressResponse shippingAddress,
            List<OrderItemResponse> items) {

        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(
            PaymentStatus paymentStatus) {

        this.paymentStatus = paymentStatus;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }


    public ShippingAddressResponse getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(
            ShippingAddressResponse shippingAddress) {

        this.shippingAddress = shippingAddress;
    }


    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItemResponse> items) {

        this.items = items;
    }
}