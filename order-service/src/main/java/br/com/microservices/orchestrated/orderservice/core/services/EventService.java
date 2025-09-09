package br.com.microservices.orchestrated.orderservice.core.services;

import br.com.microservices.orchestrated.orderservice.core.models.Event;
import br.com.microservices.orchestrated.orderservice.core.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventService {
    private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

    private  final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public void notifyEnding(Event event) {
        event.setOrderId(event.getOrderId());
        event.setCreateAt(LocalDateTime.now());

        save(event);

        LOG.info("Order {} with saga notified! TransactionId: {}", event.getOrderId(), event.getTransactionId());
    }
}
