package br.com.microservices.orchestrated.orderservice.core.models;

import java.time.LocalDateTime;

public class History {

    private String source;

    private String status;

    private String message;

    private LocalDateTime createdAt;

    public History() {
    }

    public History(String source, LocalDateTime createdAt, String message, String status) {
        this.source = source;
        this.createdAt = createdAt;
        this.message = message;
        this.status = status;
    }

    private History(Builder builder) {
        this.source = builder.source;
        this.status = builder.status;
        this.message = builder.message;
        this.createdAt = builder.createdAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder {
        private String source;
        private String status;
        private String message;
        private LocalDateTime createdAt;

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public History build() {
            return new History(this);
        }
    }
}
