package br.com.microservices.orchestrated.orderservice.core.controllers;

import br.com.microservices.orchestrated.orderservice.core.dtos.OrderRequest;
import br.com.microservices.orchestrated.orderservice.core.models.Order;
import br.com.microservices.orchestrated.orderservice.core.services.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private  final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }
}
