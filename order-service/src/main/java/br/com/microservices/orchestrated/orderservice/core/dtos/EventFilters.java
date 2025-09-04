package br.com.microservices.orchestrated.orderservice.core.dtos;

public class EventFilters {

    private String orderId;
    private String transactionId;

    public EventFilters() {
    }

    public EventFilters(String transactionId, String orderId) {
        this.transactionId = transactionId;
        this.orderId = orderId;
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
}
