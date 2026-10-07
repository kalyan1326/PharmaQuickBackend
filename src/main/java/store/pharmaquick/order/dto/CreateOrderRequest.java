package store.pharmaquick.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreateOrderRequest {

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressRequest shippingAddress;


    // =========================
    // CONSTRUCTOR
    // =========================

    public CreateOrderRequest() {
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public ShippingAddressRequest getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(
            ShippingAddressRequest shippingAddress) {

        this.shippingAddress = shippingAddress;
    }
}