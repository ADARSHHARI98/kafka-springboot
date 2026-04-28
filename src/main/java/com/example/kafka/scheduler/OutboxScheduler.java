package com.example.kafka.scheduler;

import com.example.kafka.model.OrderEvent;
import com.example.kafka.model.OutboxEvent;
import com.example.kafka.producer.OrderProducer;
import com.example.kafka.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class OutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);

    private final OutboxRepository outboxRepository;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;

    public OutboxScheduler(OutboxRepository outboxRepository, OrderProducer orderProducer, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.orderProducer = orderProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByProcessedFalse();
        
        if (!pendingEvents.isEmpty()) {
            log.info("Found {} pending outbox events to process", pendingEvents.size());
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                // 1. Deserialize the payload
                OrderEvent orderEvent = objectMapper.readValue(event.getPayload(), OrderEvent.class);
                
                // 2. Send to Kafka and wait for completion to guarantee at-least-once delivery
                log.info("Publishing outbox event for order [{}]", orderEvent.getOrderId());
                orderProducer.sendOrder(orderEvent).get(); // Blocks until successful publish
                
                // 3. Mark as processed
                event.setProcessed(true);
                outboxRepository.save(event);
                log.info("Successfully processed outbox event for order [{}]", orderEvent.getOrderId());
                
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to publish outbox event id [{}] due to Kafka error: {}", event.getId(), e.getMessage());
                // In case of interruption, restore interrupted status
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                log.error("Failed to process outbox event id [{}]", event.getId(), e);
            }
        }
    }
}
