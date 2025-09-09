package br.com.microservices.orchestrated.orderservice.core.repositories;

import br.com.microservices.orchestrated.orderservice.core.models.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByOrderByCreatedAtDesc();

    Optional<Event> findTop1byOrderIdOderByCreatedAtDesc(String orderId);

    Optional<Event> findTop1byTransactionIdOderByCreatedAtDesc(String transactionId);

}
