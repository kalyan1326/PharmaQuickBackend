package store.pharmaquick.cart.dto;

import java.math.BigDecimal;

public class CartResponse {

    private Long cartId;

    private Long productId;

    private String productName;

    private String category;

    private String brand;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;

    private String imageUrl;

    private Integer availableStock;

    public CartResponse() {
    }

    public CartResponse(
            Long cartId,
            Long productId,
            String productName,
            String category,
            String brand,
            BigDecimal price,
            Integer quantity,
            BigDecimal subtotal,
            String imageUrl,
            Integer availableStock) {

        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.imageUrl = imageUrl;
        this.availableStock = availableStock;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
}