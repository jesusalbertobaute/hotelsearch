package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

class SearchKafkaProducerTest {

	private OutboxEventRepository outboxRepository;
	private KafkaPublisherProxy kafkaPublisher;
	private ExecutorService executorService;
	private ObjectMapper objectMapper;

	private SearchKafkaProducer producer;

	@BeforeEach
	void setUp() {
		this.outboxRepository = mock(OutboxEventRepository.class);
		this.kafkaPublisher = mock(KafkaPublisherProxy.class);
		this.executorService = Executors.newFixedThreadPool(2); 
		this.objectMapper = new ObjectMapper();

		this.producer = new SearchKafkaProducer(
				outboxRepository,
				kafkaPublisher,
				executorService,
				objectMapper
				);
		
		this.producer.setTopic("dummy-topic");
	}

	@Test
	void publishShouldProcessOutboxEvents() throws Exception {
		
		String eventId = "EVENT1234567890_ABCDEFGHIJKLMN";
		String searchId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		SearchEvent searchEvent = new SearchEvent(eventId, searchId, null);
		String payload = objectMapper.writeValueAsString(searchEvent);

		OutboxEventEntity entity = new OutboxEventEntity();
		entity.setEventId(eventId);
		entity.setPayload(payload);

		when(outboxRepository.fetchNextBatchIds(anyInt())).thenReturn(List.of(eventId));
		when(outboxRepository.markBatchAsProcessing(anyList())).thenReturn(1);
		when(outboxRepository.findByEventIdIn(List.of(eventId))).thenReturn(List.of(entity));
		when(outboxRepository.saveAll(anyList())).thenReturn(null);

		producer.publish();

		ArgumentCaptor<SearchEvent> captor = ArgumentCaptor.forClass(SearchEvent.class);
		verify(kafkaPublisher, times(1)).publishEvent(captor.capture());

		SearchEvent publishedEvent = captor.getValue();
		assert publishedEvent.eventId().equals(eventId);

		assert entity.isPublished();
		assert !entity.isProcessing();

		verify(outboxRepository, times(1)).saveAll(List.of(entity));
	}

}
