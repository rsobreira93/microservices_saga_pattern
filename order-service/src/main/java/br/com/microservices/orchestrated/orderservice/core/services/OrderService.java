package br.com.microservices.orchestrated.orderservice.core.services;

import br.com.microservices.orchestrated.orderservice.core.dtos.OrderRequest;
import br.com.microservices.orchestrated.orderservice.core.models.Event;
import br.com.microservices.orchestrated.orderservice.core.models.Order;
import br.com.microservices.orchestrated.orderservice.core.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.core.repositories.OrderRepository;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private static final String TRANSACTION_ID_PATTERN = "%s_%s";

    private final OrderRepository orderRepository;
    private final JsonUtil jsonUtil;
    private  final SagaProducer sagaProducer;
    private final EventService eventService;

    public OrderService(OrderRepository orderRepository, JsonUtil jsonUtil, SagaProducer sagaProducer, EventService eventService) {
        this.orderRepository = orderRepository;
        this.jsonUtil = jsonUtil;
        this.sagaProducer = sagaProducer;
        this.eventService = eventService;
    }

    public Order createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .products(orderRequest.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(
                        String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
                )
                .build();

        orderRepository.save(order);

        sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));

        return order;
    }

    private Event createPayload(Order order) {
        Event event = Event.builder()
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .payload(order)
                .createdAt(LocalDateTime.now())
                .build();

        eventService.save(event);

        return event;
    }
}
