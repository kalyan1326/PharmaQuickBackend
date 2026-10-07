package store.pharmaquick.order.entity;

import jakarta.persistence.*;
import store.pharmaquick.product.entity.Product;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    // Order this item belongs to
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    // Product that was purchased
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    // Product name at the time of purchase
    @Column(nullable = false)
    private String productName;

    // Price at the time of purchase
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    // Quantity purchased
    @Column(nullable = false)
    private Integer quantity;

    // price × quantity
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal subtotal;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public OrderItem() {
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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