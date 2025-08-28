package br.com.microservices.orchestrated.orchestratorservice.core.dtos;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;

import java.time.LocalDateTime;

public class History {

    private EEventSource source;

    private ESagaStatus status;

    private String message;

    private LocalDateTime createdAt;

    public History() {
    }

    public History(EEventSource source, LocalDateTime createdAt, String message, ESagaStatus status) {
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

    public EEventSource getSource() {
        return source;
    }

    public void setSource(EEventSource source) {
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

    public ESagaStatus getStatus() {
        return status;
    }

    public void setStatus(ESagaStatus status) {
        this.status = status;
    }

    public static class Builder {
        private EEventSource source;
        private ESagaStatus status;
        private String message;
        private LocalDateTime createdAt;

        public Builder source(EEventSource source) {
            this.source = source;
            return this;
        }

        public Builder status(ESagaStatus status) {
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
