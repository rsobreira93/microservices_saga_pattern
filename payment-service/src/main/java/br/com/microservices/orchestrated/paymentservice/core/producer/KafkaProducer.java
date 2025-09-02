package br.com.microservices.orchestrated.paymentservice.core.producer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.orchestrator}")
    private String orchestratorTopic;


    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String payload) {
        try {
            LOG.info("Sending event to topic {} with data {}", orchestratorTopic, payload);

            kafkaTemplate.send(orchestratorTopic, payload);
        } catch (Exception ex) {
            LOG.error("Error trying to send data to topic {} with data {}", orchestratorTopic, payload, ex);
        }
    }
}
