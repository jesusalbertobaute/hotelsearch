package com.mindata.hotelsearch.application.usecase;

import java.time.LocalDate;
import java.util.List;

import com.mindata.hotelsearch.application.port.input.SaveSearchInputPort;
import com.mindata.hotelsearch.application.port.output.SaveSearchOutputPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;


public class SaveSearchUseCase implements SaveSearchInputPort{
	
	private final SaveSearchOutputPort saveSearch;

	public SaveSearchUseCase(SaveSearchOutputPort saveSearch) {
		this.saveSearch = saveSearch;
	}

	@Override
	public String save(String hotelId,
	        LocalDate checkIn,
	        LocalDate checkOut,
	        List<Integer> ages) {
		
		SearchDetails searchDetails = new SearchDetails(hotelId,checkIn,checkOut,ages);
		
		Search searchCreated = Search.create(searchDetails);
		
		this.saveSearch.save(searchCreated);
		
		return searchCreated.getSearchId();
	}

}
