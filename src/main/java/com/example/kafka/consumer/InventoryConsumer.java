package com.example.kafka.consumer;

import com.example.kafka.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    @KafkaListener(topics = "orders", groupId = "inventory-group")
    public void consume(OrderEvent event) {
        log.info("📦 [Inventory] Reserving {}x {} for order {}", 
                 event.getQuantity(), event.getProduct(), event.getOrderId());
    }
}
