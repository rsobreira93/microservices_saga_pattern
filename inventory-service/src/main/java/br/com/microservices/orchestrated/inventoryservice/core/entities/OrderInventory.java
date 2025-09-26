package br.com.microservices.orchestrated.inventoryservice.core.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders_inventories")
public class OrderInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Integer orderQuantity;

    @Column(nullable = false)
    private Integer oldQuantity;

    @Column(nullable = false)
    private Integer newQuantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt =  LocalDateTime.now();;
    }

    public OrderInventory() {
    }

    public static OrderInventoryBuilder builder() {
        return new OrderInventoryBuilder();
    }

    public OrderInventory(Integer id, Inventory inventory, String orderId, String transactionId, Integer orderQuantity, Integer oldQuantity, Integer newQuantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.inventory = inventory;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.orderQuantity = orderQuantity;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Integer getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(Integer oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class OrderInventoryBuilder {
        private Integer id;
        private Inventory inventory;
        private String orderId;
        private String transactionId;
        private Integer orderQuantity;
        private Integer oldQuantity;
        private Integer newQuantity;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public OrderInventoryBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public OrderInventoryBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderInventoryBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public OrderInventoryBuilder inventory(Inventory inventory) {
            this.inventory = inventory;
            return this;
        }

        public OrderInventoryBuilder orderQuantity(Integer orderQuantity) {
            this.orderQuantity = orderQuantity;
            return this;
        }

        public OrderInventoryBuilder oldQuantity(Integer oldQuantity) {
            this.oldQuantity = oldQuantity;
            return this;
        }

        public OrderInventoryBuilder newQuantity(Integer newQuantity) {
            this.newQuantity = newQuantity;
            return this;
        }

        public OrderInventoryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrderInventoryBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public OrderInventory build() {
            return new OrderInventory(id, inventory, orderId, transactionId, orderQuantity, oldQuantity, newQuantity, createdAt, updatedAt);
        }
    }
}
