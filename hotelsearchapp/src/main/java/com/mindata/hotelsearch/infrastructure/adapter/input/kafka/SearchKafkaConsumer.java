package com.mindata.hotelsearch.infrastructure.adapter.input.kafka;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.application.port.input.UpdateSearchCountInputPort;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;

@Service
public class SearchKafkaConsumer {
	private static final Logger log = LoggerFactory.getLogger(SearchKafkaConsumer.class);

	private final UpdateSearchCountInputPort updateSearchCountUseCase;
	private final ExecutorService consumerExecutor;
	private final KafkaTemplate<String, SearchEvent> kafkaTemplate;

	public SearchKafkaConsumer(UpdateSearchCountInputPort updateSearchCountUseCase,
			@Qualifier("consumerExecutor") ExecutorService consumerExecutor,
			KafkaTemplate<String, SearchEvent> kafkaTemplate) {
		this.updateSearchCountUseCase = updateSearchCountUseCase;
		this.consumerExecutor = consumerExecutor;
		this.kafkaTemplate = kafkaTemplate;
	}

	@RetryableTopic(
			attempts = "3",
			backoff = @Backoff(delay = 1000, multiplier = 2),
			dltTopicSuffix = "_consumer_dlq",
			autoCreateTopics = "true"
			)
	@KafkaListener(
			topics = "hotel_availability_searches",
			groupId = "hotel-search-consumer-group",
			concurrency = "3"
			)
	public void consume(SearchEvent searchEvent) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Consumed Event eventId {} of searchId {} ", searchEvent.eventId(), searchEvent.searchId());
			}

			this.updateSearchCountUseCase.incrementSearchCount(
					searchEvent.eventId(),
					searchEvent.searchId(),
					searchEvent.searchEventDetails().hotelId(),
					searchEvent.searchEventDetails().checkIn(),
					searchEvent.searchEventDetails().checkOut(),
					searchEvent.searchEventDetails().ages()
					);

		} catch (Exception e) {
			log.error("Error processing event {}, will be retried or sent to DLQ", searchEvent.eventId(), e);
			throw e;
		}
	}

}
