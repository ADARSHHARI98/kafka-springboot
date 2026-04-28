package com.example.kafka.controller;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.kafka.model.Order;
import com.example.kafka.model.OrderEvent;
import com.example.kafka.service.OrderService;
import com.example.kafka.repository.OrderRepository;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderEvent event) {
        event.setOrderId(UUID.randomUUID().toString());
        event.setStatus("PLACED");

        orderService.createOrderAndOutboxEvent(event);

        return ResponseEntity.ok("Order queued: " + event.getOrderId());
    }

    @GetMapping("/{id}")
    @Cacheable(value = "orders", key = "#id")
    public Order getOrder(@PathVariable Long id) {
        System.out.println("Fetching order from database for ID: " + id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}
// @CachePut:
// Used on"Update"methods (like updateOrder). It always executes the method and updates the Redis entry with the new data.
// @CacheEvict: Used on "Delete" methods. It removes the entry from Redis so that stale data isn't served.
