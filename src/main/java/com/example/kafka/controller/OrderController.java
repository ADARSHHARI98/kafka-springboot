package com.example.kafka.controller;

import com.example.kafka.model.OrderEvent;
import com.example.kafka.producer.OrderProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderEvent event) {
        event.setOrderId(UUID.randomUUID().toString());
        event.setStatus("PLACED");

        orderProducer.sendOrder(event);

        return ResponseEntity.ok("Order queued: " + event.getOrderId());
    }
}
