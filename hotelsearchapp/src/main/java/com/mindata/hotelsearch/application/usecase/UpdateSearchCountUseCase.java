package com.mindata.hotelsearch.application.usecase;

import java.time.LocalDate;
import java.util.List;

import com.mindata.hotelsearch.application.port.input.UpdateSearchCountInputPort;
import com.mindata.hotelsearch.application.port.output.UpdateSearchCountOutputPort;
import com.mindata.hotelsearch.domain.exception.DomainException;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;


public class UpdateSearchCountUseCase implements UpdateSearchCountInputPort {
    private final UpdateSearchCountOutputPort updateSearchCountOutput;
    
	public UpdateSearchCountUseCase(UpdateSearchCountOutputPort updateSearchCountOutput) {
		this.updateSearchCountOutput = updateSearchCountOutput;
	}

	@Override
	public void incrementSearchCount(String requestId, 
			String searchId, String hotelId, LocalDate checkIn,
			LocalDate checkOut, List<Integer> ages) {
		if (requestId == null || requestId.isBlank()) {
            throw new DomainException("requestId cannot be null or blank");
        }
		Search search = this.buildModel(searchId, hotelId, checkIn, checkOut, ages);
		this.updateSearchCountOutput.incrementSearchCount(requestId, search);
	}
	
	
	protected Search buildModel(String searchId, String hotelId, LocalDate checkIn,
			LocalDate checkOut, List<Integer> ages) {
		SearchDetails searchDetails = new SearchDetails(hotelId,checkIn,checkOut,ages);
		return Search.create(searchId,searchDetails);
	}



}
