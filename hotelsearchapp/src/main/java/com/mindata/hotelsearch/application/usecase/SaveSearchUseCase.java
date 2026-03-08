package com.mindata.hotelsearch.application.usecase;

import java.time.LocalDate;
import java.util.List;

import com.mindata.hotelsearch.application.annotation.UseCase;
import com.mindata.hotelsearch.application.port.input.SaveSearchPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;

@UseCase
public class SaveSearchUseCase implements SaveSearchPort{

	public SaveSearchUseCase() {
		
	}

	@Override
	public String save(String hotelId,
	        LocalDate checkIn,
	        LocalDate checkOut,
	        List<Integer> ages) {
		
		SearchDetails searchDetails = new SearchDetails(hotelId,checkIn,checkOut,ages);
		
		Search searchCreated = Search.create(searchDetails);
		
		return searchCreated.getSearchId();
	}

}
