package com.mindata.hotelsearch.infrastructure.adapter.mapping;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchCountResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchDetailsResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEventDetails;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
	
	public static SearchDetails toSearchDetails(String hotelId,
			LocalDate checkIn,
			LocalDate checkOut,
			List<Integer> ages) {
		return new SearchDetails(hotelId,
				checkIn,
				checkOut,
				ages);
	}
	
	public static Search toSearch(ReservationSearchEntity reservationSearchEntity) {
		SearchDetails searchDetails = toSearchDetails(reservationSearchEntity.getHotelId(),
				reservationSearchEntity.getCheckIn(),reservationSearchEntity.getCheckOut(),
				reservationSearchEntity.getAges());
		return Search.create(reservationSearchEntity.getSearchId(),
				searchDetails,
				reservationSearchEntity.getCount());
	}
	
	public static SearchDetailsResponseDTO toSearchDetailsResponseDTO(SearchDetails searchDetails) {
		if (searchDetails==null) {
			return null;
		}
		return new SearchDetailsResponseDTO(searchDetails.hotelId(),searchDetails.checkIn(),
				searchDetails.checkOut(),searchDetails.ages());
	}

	public static SearchCountResponseDTO toSeachCountResponseDTO(Search search) {
		SearchDetailsResponseDTO searchDetailsResponseDTO = toSearchDetailsResponseDTO(search.getSearchData());
		return new SearchCountResponseDTO(search.getSearchId(),searchDetailsResponseDTO,search.getCount());
	}
}
