package store.pharmaquick.order.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long orderItemId;

    private Long productId;

    private String productName;

    private String imageUrl;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;


    // =========================
    // CONSTRUCTOR
    // =========================

    public OrderItemResponse() {
    }

    public OrderItemResponse(
            Long orderItemId,
            Long productId,
            String productName,
            String imageUrl,
            BigDecimal price,
            Integer quantity,
            BigDecimal subtotal) {

        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}