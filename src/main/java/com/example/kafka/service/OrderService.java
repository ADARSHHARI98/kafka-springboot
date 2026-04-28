package com.example.kafka.service;

import com.example.kafka.model.Order;
import com.example.kafka.model.OrderEvent;
import com.example.kafka.model.OutboxEvent;
import com.example.kafka.repository.OrderRepository;
import com.example.kafka.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void createOrderAndOutboxEvent(OrderEvent event) {
        // Save the actual order
        orderRepository.save(new Order(event));
        
        // Save the outbox event payload to guarantee atomic writes with the order
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent(event.getOrderId(), payload);
            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OrderEvent for outbox", e);
        }
    }
}
