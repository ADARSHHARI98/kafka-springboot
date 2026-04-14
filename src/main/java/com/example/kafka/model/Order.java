package com.example.kafka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "orders")
public class Order implements Serializable {// Convert object → bytes → store/send → reconstruct object

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    @Column(name = "product")
    private String product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "status", length = 50)
    private String status;

    // No-arg constructor (required by JPA)
    public Order() {
    }

    // Convenience constructor from OrderEvent
    public Order(OrderEvent event) {
        this.orderId = event.getOrderId();
        this.product = event.getProduct();
        this.quantity = event.getQuantity();
        this.status = event.getStatus();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }
}