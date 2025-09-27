package br.com.microservices.orchestrated.orchestratorservice.core.dtos;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

public class Event {

    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private EEventSource source;
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createAt;

    public void addHistory(History history) {
        if (isEmpty(eventHistory)) {
            eventHistory = new ArrayList<>();
        }

        eventHistory.add(history);
    }

    public Event() {
    }

    public Event(String id, LocalDateTime createAt, ESagaStatus status, List<History> eventHistory, EEventSource source, Order payload, String orderId, String transactionId) {
        this.id = id;
        this.createAt = createAt;
        this.status = status;
        this.eventHistory = eventHistory;
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
        this.eventHistory = builder.eventHistory;
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

    public List<History> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(List<History> eventHistory) {
        this.eventHistory = eventHistory;
    }

    public ESagaStatus getStatus() {
        return status;
    }

    public void setStatus(ESagaStatus status) {
        this.status = status;
    }

    public EEventSource getSource() {
        return source;
    }

    public void setSource(EEventSource source) {
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
        private EEventSource source;
        private ESagaStatus status;
        private List<History> eventHistory;
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

        public Builder source(EEventSource source) {
            this.source = source;
            return this;
        }

        public Builder status(ESagaStatus status) {
            this.status = status;
            return this;
        }

        public Builder eventHistory(List<History> eventHistory) {
            this.eventHistory = eventHistory;
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
