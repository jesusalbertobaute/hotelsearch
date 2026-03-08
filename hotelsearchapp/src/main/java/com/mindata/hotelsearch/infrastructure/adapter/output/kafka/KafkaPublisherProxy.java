package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaPublisherProxy {
	private static final Logger log = LoggerFactory.getLogger(KafkaPublisherProxy.class);
	private final KafkaTemplate<String, SearchEvent> kafkaTemplate;
	
	@Value("${kafka.topic.searches}")
	private String topic;
	
	public KafkaPublisherProxy(KafkaTemplate<String, SearchEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Retry(name = "kafkaPublisherRetry")
    @CircuitBreaker(name = "kafkaPublisherCB", fallbackMethod = "fallbackPublish")
    public void publishEvent(SearchEvent event) throws InterruptedException, ExecutionException {
        kafkaTemplate.send(topic, event.eventId(), event).get(); 
    }

    public void fallbackPublish(SearchEvent event, Throwable ex) {
        log.error("Circuit breaker triggered for event {}. Will retry later.", event.eventId(), ex);
    }


}
