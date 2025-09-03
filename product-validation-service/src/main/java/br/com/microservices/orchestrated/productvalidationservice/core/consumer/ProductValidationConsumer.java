package br.com.microservices.orchestrated.productvalidationservice.core.consumer;


import br.com.microservices.orchestrated.productvalidationservice.core.dtos.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductValidationConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ProductValidationConsumer.class);

    private final JsonUtil jsonUtil;

    public ProductValidationConsumer(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-success}"
    )
    public void consumeSuccessEvent(String payload) {
        LOG.info("Receiving success event from event {} from product-validation-success topic", payload);

        Event event = jsonUtil.toEvent(payload);

        LOG.info(event.toString());

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-fail}"
    )
    public void consumeFailEvent(String payload) {
        LOG.info("Receiving rollback event {} product-validation-fail topic", payload);

        Event event = jsonUtil.toEvent(payload);

        LOG.info(event.toString());

    }

}
