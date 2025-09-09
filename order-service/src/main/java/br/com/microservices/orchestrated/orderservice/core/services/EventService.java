package br.com.microservices.orchestrated.orderservice.core.services;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.dtos.EventFilters;
import br.com.microservices.orchestrated.orderservice.core.models.Event;
import br.com.microservices.orchestrated.orderservice.core.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<Event> findAll(){
        return eventRepository.findAllByOrderByCreatedAtDesc();
    }

    public Event findByFilters(EventFilters eventFilters) {
        validateEmptyFilters(eventFilters);

        if(!ObjectUtils.isEmpty(eventFilters.getOrderId())) {
            return findByOrderId(eventFilters.getOrderId());
        } else {
            return  findByTransactionId(eventFilters.getTransactionId());
        }
    }

    private Event findByOrderId(String orderId) {
        return eventRepository.findTop1byOrderIdOderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new NotFoundException("Event not found by orderId."));
    }

    private Event findByTransactionId(String transactionId) {
        return eventRepository.findTop1byTransactionIdOderByCreatedAtDesc(transactionId)
                .orElseThrow(() -> new NotFoundException("Event not found by transactionId."));
    }

    private void validateEmptyFilters(EventFilters filters) {
        if(ObjectUtils.isEmpty(filters.getOrderId()) && ObjectUtils.isEmpty(filters.getTransactionId())) {
            throw  new ValidationException("OrderId or TransactionId must be informed. ");
        }
    }
}
