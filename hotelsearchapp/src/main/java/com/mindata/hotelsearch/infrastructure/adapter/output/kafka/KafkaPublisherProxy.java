package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;

@Service
public class KafkaPublisherProxy {
	private static final Logger log = LoggerFactory.getLogger(KafkaPublisherProxy.class);
	private final KafkaTemplate<String, SearchEvent> kafkaTemplate;
	
	@Value("${kafka.topic.searches}")
	private String topic;
	
	public KafkaPublisherProxy(KafkaTemplate<String, SearchEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@RetryableTopic(
		    attempts = "5",
		    backoff = @Backoff(delay = 1000, multiplier = 2),
		    dltTopicSuffix = "_producer_dlq",
		    autoCreateTopics = "true"
	)
    public void publishEvent(SearchEvent event) throws InterruptedException, ExecutionException {
        this.kafkaTemplate.send(topic, event.eventId(), event).get(); 
    }

}
