package com.example.kafka.consumer;

import com.example.kafka.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void consume(OrderEvent event) {
        log.info("📧 [Notification] Order received: {}", event.toString());
    }
}
