package com.mindata.hotelsearch.infrastructure.adapter.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchCountResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchDetailsResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEventDetails;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;

class SearchOutputMapperTest {

    @Test
    void toSearchEventDetailsMapsCorrectly() {

        SearchDetails details = new SearchDetails(
                "hotel1",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                List.of(30, 25)
        );

        SearchEventDetails result = SearchOutputMapper.toSearchEventDetails(details);

        assertEquals("hotel1", result.hotelId());
        assertEquals(LocalDate.of(2026, 6, 1), result.checkIn());
        assertEquals(LocalDate.of(2026, 6, 5), result.checkOut());
        assertEquals(List.of(30, 25), result.ages());
    }

    @Test
    void toSearchEventMapsCorrectly() {
    	final String eventId = "550e8400-e29b-41d4-a716-446655440000";
     
        final String searchId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; 

        SearchDetails details = new SearchDetails(
                "hotel1",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                List.of(30)
        );

        Search search = Search.create(searchId, details, 2);

        SearchEvent event = SearchOutputMapper.toSearchEvent(eventId, search);

        assertEquals(eventId, event.eventId());
        assertEquals(searchId, event.searchId());
        assertNotNull(event.searchEventDetails());
    }

    @Test
    void toSearchDetailsCreatesObject() {

        SearchDetails details = SearchOutputMapper.toSearchDetails(
                "hotelX",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 3),
                List.of(20, 21)
        );

        assertEquals("hotelX", details.hotelId());
        assertEquals(LocalDate.of(2026, 7, 1), details.checkIn());
        assertEquals(LocalDate.of(2026, 7, 3), details.checkOut());
        assertEquals(List.of(20, 21), details.ages());
    }

    @Test
    void toSearchMapsReservationEntity() {
    	final String searchId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; 
        ReservationSearchEntity entity = new ReservationSearchEntity();
        entity.setSearchId(searchId);
        entity.setHotelId("hotelY");
        entity.setCheckIn(LocalDate.of(2026, 8, 1));
        entity.setCheckOut(LocalDate.of(2026, 8, 5));
        entity.setAges(List.of(30));
        entity.setCount(5);

        Search result = SearchOutputMapper.toSearch(entity);

        assertEquals(searchId, result.getSearchId());
        assertEquals(5, result.getCount());
        assertEquals("hotelY", result.getSearchData().hotelId());
    }

    @Test
    void toSearchDetailsResponseDTOReturnsNullWhenInputNull() {

        SearchDetailsResponseDTO result =
                SearchOutputMapper.toSearchDetailsResponseDTO(null);

        assertNull(result);
    }

    @Test
    void toSearchDetailsResponseDTOMapsCorrectly() {

        SearchDetails details = new SearchDetails(
                "hotelZ",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 2),
                List.of(40)
        );

        SearchDetailsResponseDTO dto =
                SearchOutputMapper.toSearchDetailsResponseDTO(details);

        assertEquals("hotelZ", dto.hotelId());
        assertEquals(LocalDate.of(2026, 9, 1), dto.checkIn());
        assertEquals(LocalDate.of(2026, 9, 2), dto.checkOut());
        assertEquals(List.of(40), dto.ages());
    }

    @Test
    void toSearchCountResponseDTOMapsCorrectly() {
    	final String searchId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; 
        SearchDetails details = new SearchDetails(
                "hotel1",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                List.of(30)
        );

        Search search = Search.create(searchId, details, 10);

        SearchCountResponseDTO dto =
                SearchOutputMapper.toSeachCountResponseDTO(search);

        assertEquals(searchId, dto.searchId());
        assertEquals(10, dto.count());
        assertNotNull(dto.search());
    }
}