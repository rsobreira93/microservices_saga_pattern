package br.com.microservices.orchestrated.productvalidationservice.core.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private  String id;

    private List<OrderProducts> products;

    private String transactionId;

    private LocalDateTime createdAt;

    private  double totalAmount;

    private  int totalItems;

    public Order() {
    }

    public Order(String id, int totalItems, double totalAmount, LocalDateTime createdAt, String transactionId, List<OrderProducts> products) {
        this.id = id;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
        this.products = products;
    }

    private Order(Builder builder) {
        this.id = builder.id;
        this.products = builder.products;
        this.createdAt = builder.createdAt;
        this.totalAmount = builder.totalAmount;
        this.totalItems = builder.totalItems;
        this.transactionId = builder.transactionId;
    }

    public String getId() {
        return id;
    }

    public List<OrderProducts> getProducts() {
        return products;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public static class Builder {
        private String id;
        private List<OrderProducts> products;
        private LocalDateTime createdAt;
        private  String transactionId;
        private double totalAmount;
        private int totalItems;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder products(List<OrderProducts> products) {
            this.products = products;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder totalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder totalItems(int totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
