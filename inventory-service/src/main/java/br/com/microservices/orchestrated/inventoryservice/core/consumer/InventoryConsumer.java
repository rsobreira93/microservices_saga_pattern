package br.com.microservices.orchestrated.inventoryservice.core.consumer;


import br.com.microservices.orchestrated.inventoryservice.core.dtos.Event;
import br.com.microservices.orchestrated.inventoryservice.core.services.InventoryService;
import br.com.microservices.orchestrated.inventoryservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryConsumer.class);

    private final JsonUtil jsonUtil;
    private final InventoryService inventoryService;

    public InventoryConsumer(JsonUtil jsonUtil, InventoryService inventoryService) {
        this.jsonUtil = jsonUtil;
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-success}"
    )
    public void consumeSuccessEvent(String payload) {
        LOG.info("Receiving success event from event {} from inventory-success topic", payload);

        Event event = jsonUtil.toEvent(payload);

        inventoryService.updateInventory(event);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-fail}"
    )
    public void consumeFailEvent(String payload) {
        LOG.info("Receiving rollback event {} inventory-fail topic", payload);

        Event event = jsonUtil.toEvent(payload);

        inventoryService.rollbackInventory(event);

    }

}
