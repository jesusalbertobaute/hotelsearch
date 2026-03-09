package com.mindata.hotelsearch.infrastructure.adapter.input.kafka;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.application.port.input.UpdateSearchCountInputPort;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;

@Service
public class SearchKafkaConsumer {
	private static final Logger log = LoggerFactory.getLogger(SearchKafkaConsumer.class);
	private final UpdateSearchCountInputPort updateSearchCountUseCase;
	
	private final ExecutorService consumerExecutor;
	
	@Value("${kafka.topic.searches}")
	private String topic;

	public SearchKafkaConsumer(UpdateSearchCountInputPort updateSearchCountUseCase, 
			@Qualifier("consumerExecutor") ExecutorService consumerExecutor) {
		this.updateSearchCountUseCase = updateSearchCountUseCase; 
		this.consumerExecutor = consumerExecutor;
	}
	
	@KafkaListener(topics = "hotel_availability_searches",
			      groupId = "hotel-search-consumer-group")
    public void consume(SearchEvent searchEvent) {
		log.info("Consumend Event eventId {} of searchId {} ", 
				searchEvent.eventId(),searchEvent.searchId());
        consumerExecutor.submit(() -> updateSearchCountUseCase.incrementSearchCount(
			searchEvent.eventId(),
			searchEvent.searchId(),
			searchEvent.searchEventDetails().hotelId(),
			searchEvent.searchEventDetails().checkIn(),
			searchEvent.searchEventDetails().checkOut(),
			searchEvent.searchEventDetails().ages()
		));
    }

	

}
