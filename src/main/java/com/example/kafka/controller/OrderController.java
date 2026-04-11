package com.example.kafka.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.model.Order;
import com.example.kafka.model.OrderEvent;
import com.example.kafka.producer.OrderProducer;
import com.example.kafka.repository.OrderRepository;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderRepository  orderRepository;


    public OrderController(OrderProducer orderProducer, OrderRepository orderRepository) {
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderEvent event) {
        event.setOrderId(UUID.randomUUID().toString());
        event.setStatus("PLACED");

        orderRepository.save(new Order(event));
        orderProducer.sendOrder(event);

        return ResponseEntity.ok("Order queued: " + event.getOrderId());
    }
}
