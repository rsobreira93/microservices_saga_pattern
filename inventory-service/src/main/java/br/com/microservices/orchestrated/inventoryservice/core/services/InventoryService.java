package br.com.microservices.orchestrated.inventoryservice.core.services;

import br.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.inventoryservice.core.dtos.Event;
import br.com.microservices.orchestrated.inventoryservice.core.dtos.History;
import br.com.microservices.orchestrated.inventoryservice.core.dtos.Order;
import br.com.microservices.orchestrated.inventoryservice.core.dtos.OrderProducts;
import br.com.microservices.orchestrated.inventoryservice.core.entities.Inventory;
import br.com.microservices.orchestrated.inventoryservice.core.entities.OrderInventory;
import br.com.microservices.orchestrated.inventoryservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.inventoryservice.core.repositories.InventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.core.repositories.OrderInventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.inventoryservice.core.enums.ESagaStatus.*;

@Service
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);

    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final InventoryRepository inventoryRepository;
    private final OrderInventoryRepository orderInventoryRepository;

    public InventoryService(JsonUtil jsonUtil, KafkaProducer kafkaProducer, InventoryRepository inventoryRepository, OrderInventoryRepository orderInventoryRepository) {
        this.jsonUtil = jsonUtil;
        this.kafkaProducer = kafkaProducer;
        this.inventoryRepository = inventoryRepository;
        this.orderInventoryRepository = orderInventoryRepository;
    }

    public void updateInventory(Event event) {
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
            updateInventory(event.getPayload());
            handleSuccess(event);
        } catch (Exception e) {
            LOG.error("Error trying to update inventory: ", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    public void rollbackInventory(Event event) {
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);

        try {
            returnInventoryToPreviousValues(event);
            addHistory(event, "Rollback executed for inventory.");
        } catch (Exception e) {
            addHistory(event, "Rollback not executed for inventory: ".concat(e.getMessage()));
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void returnInventoryToPreviousValues(Event event) {
        orderInventoryRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .forEach(orderInventory -> {
                    Inventory inventory = orderInventory.getInventory();

                    inventory.setAvailable(orderInventory.getOldQuantity());

                    inventoryRepository.save(inventory);

                    LOG.info("Restored inventory for order {} from {} to {}",
                            event.getPayload().getId(), orderInventory.getNewQuantity(), inventory.getAvailable());
                });
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to update inventory: ".concat(message));
    }

    private void updateInventory(Order order) {
        order.getProducts()
                .forEach(product -> {
                    Inventory inventory = findInventoryByProductCode(product.getProduct().getCode());

                    checkInventory(inventory.getAvailable(), product.getQuantity());
                    inventory.setAvailable(inventory.getAvailable() - product.getQuantity());

                    inventoryRepository.save(inventory);
                });
    }

    private void checkInventory(Integer available, int orderQuantity) {
        if(orderQuantity > available) {
            throw  new ValidationException("Product is out of stock.");
        }
    }


    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Inventory updated successfully.");
    }

    private void addHistory(Event event, String message) {
        History history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

    private void createOrderInventory(Event event) {
        event.getPayload().getProducts()
                .forEach(product -> {
                    Inventory inventory = findInventoryByProductCode(product.getProduct().getCode());

                    OrderInventory orderInventory = createOrderInventory(event, product, inventory);

                    orderInventoryRepository.save(orderInventory);
                });
    }

    private OrderInventory createOrderInventory(Event event, OrderProducts product, Inventory inventory) {
        return OrderInventory
                .builder()
                .inventory(inventory)
                .oldQuantity(inventory.getAvailable())
                .orderQuantity(product.getQuantity())
                .newQuantity(inventory.getAvailable() - product.getQuantity())
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .build();
    }

    private Inventory findInventoryByProductCode(String productCode) {
        return inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ValidationException("Inventory not found by informed product"));
    }

    private void checkCurrentValidation(Event event) {
        if(orderInventoryRepository.existsByOrderIdAndTransactionId(
                event.getPayload().getId(), event.getTransactionId()
        )){
            throw new ValidationException("There's another transactionId for this validation.");
        }

    }
}
