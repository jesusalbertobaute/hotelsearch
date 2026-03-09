package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;

@Service
public class SearchKafkaProducer {
	private static final Logger log = LoggerFactory.getLogger(SearchKafkaProducer.class);
	private final OutboxEventRepository outboxRepository;
	private final KafkaPublisherProxy kafkaPublisher;
    private final ExecutorService outboxExecutor;
    private final ObjectMapper objectMapper;
	
	@Value("${kafka.topic.searches}")
	private String topic;
	
	private static final int BATCH_SIZE = 100;
	
	public SearchKafkaProducer(OutboxEventRepository outboxRepository, 
			KafkaPublisherProxy kafkaPublisher,
			@Qualifier("outboxExecutor") ExecutorService outboxExecutor, ObjectMapper objectMapper) {
		this.outboxRepository = outboxRepository;
		this.kafkaPublisher = kafkaPublisher;
		this.outboxExecutor = outboxExecutor;
		this.objectMapper = objectMapper;
	}

	@Scheduled(fixedDelay = 5000)
	public void publish() {
		List<String> batchIds = fetchAndMarkBatch();
		 
		if (batchIds.isEmpty()) {
	      log.info("No pending outbox events to process.");
	      return;
	    }
		
	    List<OutboxEventEntity> batch = outboxRepository.findByEventIdIn(batchIds);
	   
	    List<CompletableFuture<Void>> futures = batch.stream()
                .map(eventEntity -> CompletableFuture.runAsync(() -> {
                    try {
                        SearchEvent searchEvent = objectMapper.readValue(eventEntity.getPayload(), SearchEvent.class);
                        this.kafkaPublisher.publishEvent(searchEvent);

                        eventEntity.setPublished(true);
                        eventEntity.setProcessing(false);
                    } catch (Exception e) {
                        log.error("Failed to publish event {}", eventEntity.getEventId(), e);
                        eventEntity.setProcessing(false);
                    }
                }, outboxExecutor))
                .toList();
	    
	    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        this.outboxRepository.saveAll(batch);
	    
	}
	
	@Transactional
    protected List<String> fetchAndMarkBatch() {
        List<String> batchIds = this.outboxRepository.fetchNextBatchIds(BATCH_SIZE);
        if (!batchIds.isEmpty()) {
            this.outboxRepository.markBatchAsProcessing(batchIds);
            log.info("Marked {} outbox events as processing", batchIds.size());
        }
        return batchIds;
    }

}
