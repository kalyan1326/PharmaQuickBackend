package store.pharmaquick.payment.dto;

import jakarta.validation.constraints.NotNull;

public class CreatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}