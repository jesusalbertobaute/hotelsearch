package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindata.hotelsearch.application.annotation.UseCase;
import com.mindata.hotelsearch.application.port.output.SaveSearchPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.exception.PersistenceOutboxException;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.mapping.SearchOutputMapper;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class SearchSaveOutbox implements SaveSearchPort{
	
	private final OutBoxSaveProxy outboxProxy;
    private final ExecutorService executor;
    
    @Value("${kafka.topic.searches}")
	private String topic;

	@Override
	public void save(Search searchModel) {
		String eventId =  UUID.randomUUID().toString();
		SearchEvent searchEvent= SearchOutputMapper.toSearchEvent(eventId,searchModel);
		try {
			 executor.submit(() -> {
				 outboxProxy.saveEvent(searchEvent);
	            });
		}catch(Exception e) {
			 log.error("Failed to serialize search request for searchId {} and eventId {} ", searchEvent.searchId(),
					 searchEvent.eventId(), e);
		}
	}
	

	
	

}
