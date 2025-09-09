package br.com.microservices.orchestrated.orderservice.core.controllers;

import br.com.microservices.orchestrated.orderservice.core.dtos.EventFilters;
import br.com.microservices.orchestrated.orderservice.core.models.Event;
import br.com.microservices.orchestrated.orderservice.core.services.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public Event findBVyFilter(EventFilters eventFilters) {
        return eventService.findByFilters(eventFilters);
    }

    @GetMapping("all")
    public List<Event> findByFilters(){
        return eventService.findAll()
;    }
}
