package br.com.microservices.orchestrated.orderservice.core.producer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SagaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.start-saga}")
    private String startSagaTopic;


    public SagaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String payload) {
        try {
            LOG.info("Sending event to topic {} with data {}", startSagaTopic, payload);

            kafkaTemplate.send(startSagaTopic, payload);
        } catch (Exception ex) {
            LOG.error("Error trying to send data to topic {} with data {}", startSagaTopic, payload, ex);
        }
    }
}
