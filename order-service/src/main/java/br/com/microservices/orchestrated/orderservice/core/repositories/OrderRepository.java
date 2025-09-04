package br.com.microservices.orchestrated.orderservice.core.repositories;

import br.com.microservices.orchestrated.orderservice.core.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
