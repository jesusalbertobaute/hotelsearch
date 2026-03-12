package com.mindata.hotelsearch.infrastructure.adapter.output.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEventDetails;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.UpdateCountEventError;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.EventProcessedEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.EventProcessedRepository;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.ReservationSearchRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;

@Service
public class UpdateSearchCountProxy {
	private static final Logger log = LoggerFactory.getLogger(UpdateSearchCountProxy.class);
	private final EventProcessedRepository eventProcessedRepository;
	private final ReservationSearchRepository reservationSearchRepository;
	private final KafkaTemplate<String, UpdateCountEventError> kafkaTemplate;
	
	@Value("${kafka.search.update.count.dlq-topic}")
    private String dlqTopic;
	
	public UpdateSearchCountProxy(EventProcessedRepository eventProcessedRepository,
			ReservationSearchRepository reservationSearchRepository,
			KafkaTemplate<String, UpdateCountEventError> kafkaTemplate) {
		this.eventProcessedRepository = eventProcessedRepository;
		this.reservationSearchRepository = reservationSearchRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional
	@Retry(name = "updateSearchReservationRetry")
    @CircuitBreaker(name = "updateSearchReservationCB", fallbackMethod = "fallbackUpdateSearchReservation")
	public void updateSearch(String requestId,ReservationSearchEntity reservationSearchEntity) {
	    try {
	    	this.eventProcessedRepository.save(new EventProcessedEntity(requestId,LocalDateTime.now()));
	    } catch (DataIntegrityViolationException e) {
	        return;
	    }
          
	    int updated = this.reservationSearchRepository.incrementCount(reservationSearchEntity.getSearchId());
	    
	    if (updated == 0) {
	    	log.info("Save new Reservation");
	    	try {
	            this.reservationSearchRepository.save(reservationSearchEntity);
	        } catch (DataIntegrityViolationException ex) {
	        	log.error("The reservation search {} had been saved before",reservationSearchEntity.getSearchId());
	        }
	    }
	}
	
	public void fallbackUpdateSearchReservation(String requestId,ReservationSearchEntity reservationSearchEntity, Throwable ex) {
        log.error("Fallback Update Search Reservation, requestId {}, searchtId {}", requestId,reservationSearchEntity.getSearchId() , ex);
        
        try {
        	final SearchEventDetails searchEventDetails = new SearchEventDetails(reservationSearchEntity.getHotelId(),
        			reservationSearchEntity.getCheckIn(),reservationSearchEntity.getCheckOut(),reservationSearchEntity.getAges());
        			
            final UpdateCountEventError updateCountEventError = new UpdateCountEventError(requestId, 
            		reservationSearchEntity.getSearchId(), searchEventDetails,ex.getMessage(), LocalDateTime.now());
            
            this.kafkaTemplate.send(
                    this.dlqTopic,
                    requestId,
                    updateCountEventError
            );

            log.warn("Event {} sent to DLQ topic {}", requestId, dlqTopic);

        } catch (Exception exception) {
            log.error("Failed sending event {} to DLQ", requestId, exception);
        }
    }

}
