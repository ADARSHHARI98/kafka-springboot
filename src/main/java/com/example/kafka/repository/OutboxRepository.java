package com.example.kafka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.kafka.model.OutboxEvent;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByProcessedFalse();
}
