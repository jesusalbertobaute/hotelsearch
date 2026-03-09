package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindata.hotelsearch.infrastructure.adapter.exception.PersistenceOutboxException;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class OutBoxSaveProxy {
	private static final Logger log = LoggerFactory.getLogger(OutBoxSaveProxy.class);
	
	private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;
    
    @Value("${kafka.topic.searches}")
    private String topic;
  
    public OutBoxSaveProxy(OutboxEventRepository outboxRepository, ObjectMapper objectMapper) {
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Retry(name = "outboxRetry")
    @CircuitBreaker(name = "outboxCB", fallbackMethod = "fallbackSaveSearch")
    public void saveEvent(SearchEvent searchEvent) {
        try {

            String payload = objectMapper.writeValueAsString(searchEvent);

            OutboxEventEntity event = OutboxEventEntity.builder()
                    .eventId(searchEvent.eventId())
                    .searchId(searchEvent.searchId())
                    .type(topic)
                    .payload(payload)
                    .published(false)
                    .processing(false)
                    .build();

            outboxRepository.save(event);

        } catch (Exception e) {
            throw new PersistenceOutboxException(e.getMessage());
        }
    }

    public void fallbackSaveSearch(SearchEvent searchEvent, Throwable ex) {
        log.error("Fallback triggered for event {}", searchEvent.eventId(), ex);
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }

}
