package com.mindata.hotelsearch.infrastructure.adapter.output.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;
import com.mindata.hotelsearch.infrastructure.adapter.mapping.SearchOutputMapper;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.ReservationSearchRepository;

class GetSearchOutputServiceTest {

    @Mock
    private ReservationSearchRepository reservationSearchRepository;

    private GetSearchOutputService getSearchOutputService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getSearchOutputService = new GetSearchOutputService(reservationSearchRepository);
    }

    @Test
    void testFindSearch_whenNotFound_returnsDefault() {
        String searchId = "TEST_SEARCH_ID_1234567890_ABCDE_67890_12345";
        when(reservationSearchRepository.findById(searchId)).thenReturn(Optional.empty());

        Search result = getSearchOutputService.findSearch(searchId);

        assertEquals(searchId, result.getSearchId());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getSearchData());
    }

    @Test
    void testFindSearch_whenFound_returnsMapped() {
        String searchId = "TEST_SEARCH_ID_1234567890_ABCDE_67890_12345";
        ReservationSearchEntity entity = new ReservationSearchEntity();
        entity.setSearchId(searchId);

        Search expectedSearch = Search.create(searchId, new SearchDetails(
        		"hotel1",
        		LocalDate.of(2026,3,10),
        		LocalDate.of(2026,3,12),
        		List.of(30,20,11)
        		), 5L);

        when(reservationSearchRepository.findById(searchId)).thenReturn(Optional.of(entity));

        try (MockedStatic<SearchOutputMapper> mockedStatic = mockStatic(SearchOutputMapper.class)) {
            mockedStatic.when(() -> SearchOutputMapper.toSearch(entity)).thenReturn(expectedSearch);

            Search result = getSearchOutputService.findSearch(searchId);

            assertEquals(expectedSearch, result);
        }
    }
}