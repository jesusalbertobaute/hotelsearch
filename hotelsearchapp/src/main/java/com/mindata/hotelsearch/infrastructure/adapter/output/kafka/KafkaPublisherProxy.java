package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.infrastructure.adapter.exception.PublishException;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisherProxy {
	private final KafkaTemplate<String, SearchEvent> kafkaTemplate;
	
	@Value("${kafka.topic.searches}")
	private String topic;
	
	@Retry(name = "kafkaPublisherRetry")
    @CircuitBreaker(name = "kafkaPublisherCB", fallbackMethod = "fallbackPublish")
    public void publishEvent(SearchEvent event) throws InterruptedException, ExecutionException {
        kafkaTemplate.send(topic, event.eventId(), event).get(); 
    }

    public void fallbackPublish(SearchEvent event, Throwable ex) {
        log.error("Circuit breaker triggered for event {}. Will retry later.", event.eventId(), ex);
    }


}
