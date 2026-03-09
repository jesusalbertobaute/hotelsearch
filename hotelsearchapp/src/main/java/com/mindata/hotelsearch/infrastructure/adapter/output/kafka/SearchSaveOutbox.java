package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.application.port.output.SaveSearchOutputPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.mapping.SearchOutputMapper;

@Service
public class SearchSaveOutbox implements SaveSearchOutputPort{
	private static final Logger log = LoggerFactory.getLogger(SearchSaveOutbox.class);
	
	private final OutBoxSaveProxy outboxProxy;
    private final ExecutorService outboxExecutor;
    
    @Value("${kafka.topic.searches}")
	private String topic;
    
	public SearchSaveOutbox(OutBoxSaveProxy outboxProxy, 
			@Qualifier("outboxExecutor") ExecutorService outboxExecutor) {
		this.outboxProxy = outboxProxy;
		this.outboxExecutor = outboxExecutor;
	}

	@Override
	public void save(Search searchModel) {
		String eventId =  UUID.randomUUID().toString();
		SearchEvent searchEvent= SearchOutputMapper.toSearchEvent(eventId,searchModel);
		try {
			this.outboxExecutor.submit(() -> {
				 this.outboxProxy.saveEvent(searchEvent);
	            });
		}catch(Exception e) {
			 log.error("Failed to save event in outbox table for searchId {} and eventId {} ", searchEvent.searchId(),
					 searchEvent.eventId(), e);
		}
	}

}
