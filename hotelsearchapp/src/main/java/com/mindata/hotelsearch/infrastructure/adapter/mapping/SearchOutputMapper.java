package com.mindata.hotelsearch.infrastructure.adapter.mapping;

import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEventDetails;

public class SearchOutputMapper {
	
	public static SearchEventDetails toSearchEventDetails(SearchDetails searchDetails) {
		return new SearchEventDetails(searchDetails.hotelId(),
				searchDetails.checkIn(),
				searchDetails.checkOut(),
				searchDetails.ages());
	}

	public static SearchEvent toSearchEvent(String eventId,Search search) {
		SearchEventDetails searchEventDetails = toSearchEventDetails(search.getSearchData());
		return new SearchEvent(
				eventId,
				search.getSearchId(),
				searchEventDetails);
	}

}
