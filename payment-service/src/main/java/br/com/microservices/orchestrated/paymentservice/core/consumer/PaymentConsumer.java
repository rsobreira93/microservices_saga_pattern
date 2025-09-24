package br.com.microservices.orchestrated.paymentservice.core.consumer;

import br.com.microservices.orchestrated.paymentservice.core.dtos.Event;
import br.com.microservices.orchestrated.paymentservice.core.services.PaymentService;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentConsumer.class);

    private final JsonUtil jsonUtil;
    private final PaymentService paymentService;

    public PaymentConsumer(JsonUtil jsonUtil, PaymentService paymentService) {
        this.jsonUtil = jsonUtil;
        this.paymentService = paymentService;
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-success}"
    )
    public void consumeSuccessEvent(String payload) {
        LOG.info("Receiving success event from event {} from payment-success topic", payload);

        Event event = jsonUtil.toEvent(payload);

        paymentService.realizePayment(event);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-fail}"
    )
    public void consumeFailEvent(String payload) {
        LOG.info("Receiving rollback event {} payment-fail topic", payload);

        Event event = jsonUtil.toEvent(payload);

        paymentService.realizeRefound(event);
    }

}
