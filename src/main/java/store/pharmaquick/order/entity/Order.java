package store.pharmaquick.order.entity;

import jakarta.persistence.*;
import store.pharmaquick.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    // User who placed the order
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    // Total amount of the order
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalAmount;

    // Current order status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Current payment status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    // Order creation time
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Last modification time
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public Order() {
    }


    // ==========================================
    // PRE PERSIST
    // ==========================================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = OrderStatus.PENDING;
        }

        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }


    // ==========================================
    // PRE UPDATE
    // ==========================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}