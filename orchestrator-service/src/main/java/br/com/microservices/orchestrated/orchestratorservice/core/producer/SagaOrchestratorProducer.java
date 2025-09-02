package br.com.microservices.orchestrated.orchestratorservice.core.producer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaOrchestratorProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SagaOrchestratorProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SagaOrchestratorProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String payload, String topic) {
        try {
            LOG.info("Sending event to topic {} with data {}", topic, payload);

            kafkaTemplate.send(topic, payload);
        } catch (Exception ex) {
            LOG.error("Error trying to send data to topic {} with data {}", topic, payload, ex);
        }
    }
}
