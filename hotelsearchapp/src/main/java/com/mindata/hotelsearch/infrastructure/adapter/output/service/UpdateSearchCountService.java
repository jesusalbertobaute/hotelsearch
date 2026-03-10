package com.mindata.hotelsearch.infrastructure.adapter.output.service;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.application.port.output.UpdateSearchCountOutputPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;

@Service
public class UpdateSearchCountService implements UpdateSearchCountOutputPort {
    private static final Logger log = LoggerFactory.getLogger(UpdateSearchCountService.class);
	
	private final UpdateSearchCountProxy updateSearchCountProxy;
    private final ExecutorService reservationExecutor;

	public UpdateSearchCountService(UpdateSearchCountProxy updateSearchCountProxy,
			@Qualifier("reservationExecutor") ExecutorService reservationExecutor) {
		super();
		this.updateSearchCountProxy = updateSearchCountProxy;
		this.reservationExecutor = reservationExecutor;
	}

	@Override
	public void incrementSearchCount(String requestId, Search search) {
		try {
			ReservationSearchEntity reservationSearchEntity = ReservationSearchEntity.builder()
					                                          .searchId(search.getSearchId())
					                                          .hotelId(search.getSearchData().hotelId())
					                                          .checkIn(search.getSearchData().checkIn())
					                                          .checkOut(search.getSearchData().checkOut())
					                                          .ages(search.getSearchData().ages())
					                                          .count(1L).build();
					                                          
			this.reservationExecutor.submit(() -> {
				 this.updateSearchCountProxy.updateSearch(requestId, reservationSearchEntity);
	         });
		}catch(Exception e) {
			 log.error("Failed to increment reservationsearch for searchId {} and eventId {} ", search.getSearchId(),
					 requestId, e);
		}

	}

}
