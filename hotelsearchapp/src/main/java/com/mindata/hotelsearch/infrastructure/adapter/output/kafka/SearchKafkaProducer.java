package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchKafkaProducer {
	private final OutboxEventRepository outboxRepository;
	private final KafkaPublisherProxy kafkaPublisher;
    private final ExecutorService executor;
    private final ObjectMapper objectMapper;
	
	@Value("${kafka.topic.searches}")
	private String topic;
	
	private static final int BATCH_SIZE = 100;

	@Scheduled(fixedDelay = 5000)
	public void publish() {
		List<String> batchIds = fetchAndMarkBatch();
		 
		if (batchIds.isEmpty()) {
	      log.info("No pending outbox events to process.");
	      return;
	    }
		
	    List<OutboxEventEntity> batch = outboxRepository.findByEventIdIn(batchIds);
	    List<OutboxEventEntity> updatedEvents = new ArrayList<>();
	    for (OutboxEventEntity eventEntity : batch) {
            executor.submit(() -> {
                try {
                	SearchEvent searchEvent = objectMapper.readValue(eventEntity.getPayload(), SearchEvent.class);
                	kafkaPublisher.publishEvent(searchEvent);
                	eventEntity.setPublished(true);
                	eventEntity.setProcessing(false);
                	synchronized (updatedEvents) { 
                		updatedEvents.add(eventEntity); 
                    }
                } catch (Exception e) {
                    log.error("Failed to publish event {}", eventEntity.getEventId(), e);
                    eventEntity.setProcessing(false);
                    synchronized (updatedEvents) { 
                		updatedEvents.add(eventEntity); 
                    }
                }
            });
        }
	    
	}
	
	@Transactional
    protected List<String> fetchAndMarkBatch() {
        List<String> batchIds = outboxRepository.fetchNextBatchIds(BATCH_SIZE);
        if (!batchIds.isEmpty()) {
            outboxRepository.markBatchAsProcessing(batchIds);
            log.debug("Marked {} outbox events as processing", batchIds.size());
        }
        return batchIds;
    }

}
