# kafka-springboot
sample kafka using springboot

Kafka Order Processing — Spring Boot Demo
A Spring Boot application demonstrating event-driven architecture using Apache Kafka.
An order placed via REST is published to a Kafka topic and independently consumed
by a Notification service and an Inventory service.

Architecture
REST Client → Order Service (Producer) → Kafka Topic: orders
                                                  ├── Notification Consumer (group: notification-group)
                                                  └── Inventory Consumer    (group: inventory-group)
Each consumer belongs to a different consumer group, so both receive every message independently.
