package br.com.microservices.orchestrated.orderservice.core.consumer;

import br.com.microservices.orchestrated.orderservice.core.models.Event;
import br.com.microservices.orchestrated.orderservice.core.services.EventService;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(EventConsumer.class);

    private final EventService eventService;
    private final JsonUtil jsonUtil;

    public EventConsumer(JsonUtil jsonUtil, EventService eventService) {
        this.jsonUtil = jsonUtil;
        this.eventService = eventService;
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.notify-ending}"
    )
    public void consumeNotifyEndingEvent(String payload) {
        LOG.info("Receiving ending notification event from event {} notify-ending topic", payload);

        Event event = jsonUtil.toEvent(payload);

        eventService.notifyEnding(event);

    }
}
