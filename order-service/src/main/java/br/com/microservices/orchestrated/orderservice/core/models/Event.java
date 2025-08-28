package br.com.microservices.orchestrated.orderservice.core.models;

import java.time.LocalDateTime;
import java.util.List;

public class Event {

    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private String source;
    private String status;
    private List<History> eventList;
    private LocalDateTime createAt;

    public Event() {
    }

    public Event(String id, LocalDateTime createAt, String status, List<History> eventList, String source, Order payload, String orderId, String transactionId) {
        this.id = id;
        this.createAt = createAt;
        this.status = status;
        this.eventList = eventList;
        this.source = source;
        this.payload = payload;
        this.orderId = orderId;
        this.transactionId = transactionId;
    }

    private Event(Builder builder) {
        this.id = builder.id;
        this.transactionId = builder.transactionId;
        this.orderId = builder.orderId;
        this.payload = builder.payload;
        this.source = builder.source;
        this.status = builder.status;
        this.eventList = builder.eventList;
        this.createAt = builder.createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<History> getEventList() {
        return eventList;
    }

    public void setEventList(List<History> eventList) {
        this.eventList = eventList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Order getPayload() {
        return payload;
    }

    public void setPayload(Order payload) {
        this.payload = payload;
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

    public static class Builder {
        private String id;
        private String transactionId;
        private String orderId;
        private Order payload;
        private String source;
        private String status;
        private List<History> eventList;
        private LocalDateTime createAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder payload(Order payload) {
            this.payload = payload;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder eventList(List<History> eventList) {
            this.eventList = eventList;
            return this;
        }

        public Builder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }
}
