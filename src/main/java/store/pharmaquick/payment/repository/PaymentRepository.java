package store.pharmaquick.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.pharmaquick.payment.entity.Payment;
import store.pharmaquick.payment.entity.PaymentStatus;
import store.pharmaquick.order.entity.Order;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    boolean existsByOrder(Order order);

    boolean existsByRazorpayOrderId(String razorpayOrderId);

    long countByStatus(PaymentStatus status);
}