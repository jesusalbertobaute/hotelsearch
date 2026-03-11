package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    @Value("${kafka.topic.searches}")
	private String topic;
    
	public SearchSaveOutbox(OutBoxSaveProxy outboxProxy) {
		this.outboxProxy = outboxProxy;
	}

	@Override
	public void save(Search searchModel) {
		String eventId =  UUID.randomUUID().toString();
		SearchEvent searchEvent= SearchOutputMapper.toSearchEvent(eventId,searchModel);
		if (log.isDebugEnabled()) {
			log.debug("Event {} created",eventId);	
		}
		this.outboxProxy.saveEvent(searchEvent);
	}

}
