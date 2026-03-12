package com.mindata.hotelsearch.infrastructure.adapter.input.rest;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import com.mindata.hotelsearch.application.port.input.GetSearchInputPort;
import com.mindata.hotelsearch.application.port.input.SaveSearchInputPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.exception.RateLimiterException;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchCountResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchDetailsResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchRequestDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.mapping.SearchOutputMapper;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

class RestSearchControllerTest {

	@Mock
    private SaveSearchInputPort saveSearchUseCase;

    @Mock
    private GetSearchInputPort getSearchInputUseCase;

    @InjectMocks
    private RestSearchController restSearchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private String createValidSearchId() {
        return "SEARCHID_VALID_1234567890_ABCDEFGH_12345";
    }

    private String createValidHotelId() {
        return "HOTELID";
    }

    private SearchRequestDTO createSearchRequestDTO() {
        return new SearchRequestDTO(
                createValidHotelId(),
                LocalDate.of(2026,3,10),
                LocalDate.of(2026,3,15),
                List.of(25, 30)
        );
    }

    private SearchDetailsResponseDTO createSearchDetailsResponseDTO() {
        return new SearchDetailsResponseDTO(
                createValidHotelId(),
                LocalDate.of(2026,3,10),
                LocalDate.of(2026,3,15),
                List.of(25,30)
        );
    }

    @Test
    void testSaveSearchReservation_success() {
        final SearchRequestDTO request = createSearchRequestDTO();
        final String expectedSearchId = createValidSearchId();
        final SearchResponseDTO expectedSearch = new SearchResponseDTO(expectedSearchId);

        when(saveSearchUseCase.save(request.hotelId(), request.checkIn(), request.checkOut(), request.ages()))
            .thenReturn(expectedSearchId);

        final var response = restSearchController.saveSearchReservation(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expectedSearch, response.getBody());
    }

    @Test
    void testSaveSearchReservation_fallback() {
        SearchRequestDTO request = createSearchRequestDTO();
        RateLimiter mockRateLimiter = mock(RateLimiter.class);
        when(mockRateLimiter.getName()).thenReturn("saveSearchLimiter");
        RateLimiterConfig config = RateLimiterConfig.custom().build();
        when(mockRateLimiter.getRateLimiterConfig()).thenReturn(config);

        RequestNotPermitted ex = RequestNotPermitted.createRequestNotPermitted(mockRateLimiter);

        assertThrows(RateLimiterException.class,
                () -> restSearchController.fallbackSaveSearchReservation(request, ex));
    }

    @Test
    void testGetSearchReservation_countGreaterThanZero() {
        String searchId = createValidSearchId();
        Search search = Search.create(searchId, null, 5);
        when(getSearchInputUseCase.findSearch(searchId)).thenReturn(search);

        SearchDetailsResponseDTO detailsDTO = createSearchDetailsResponseDTO();
        SearchCountResponseDTO dto = new SearchCountResponseDTO(searchId, detailsDTO, 5);

        try (MockedStatic<SearchOutputMapper> mapper = org.mockito.Mockito.mockStatic(SearchOutputMapper.class)) {
            mapper.when(() -> SearchOutputMapper.toSeachCountResponseDTO(search)).thenReturn(dto);

            var response = restSearchController.getSearchReservation(searchId);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(dto, response.getBody());
        }
    }

    @Test
    void testGetSearchReservation_countZero() {
        String searchId = createValidSearchId();
        Search search = Search.create(searchId, null, 0);
        when(getSearchInputUseCase.findSearch(searchId)).thenReturn(search);

        SearchDetailsResponseDTO detailsDTO = createSearchDetailsResponseDTO();
        SearchCountResponseDTO dto = new SearchCountResponseDTO(searchId, detailsDTO, 0);

        try (MockedStatic<SearchOutputMapper> mapper = org.mockito.Mockito.mockStatic(SearchOutputMapper.class)) {
            mapper.when(() -> SearchOutputMapper.toSeachCountResponseDTO(search)).thenReturn(dto);

            var response = restSearchController.getSearchReservation(searchId);

            assertEquals(404, response.getStatusCode().value());
            assertEquals(dto, response.getBody());
        }
    }
    
    @Test
    void testGetSearchReservation_fallback() {
        String searchId = createValidSearchId();
        RateLimiter mockRateLimiter = mock(RateLimiter.class);
        when(mockRateLimiter.getName()).thenReturn("saveSearchLimiter");
        RateLimiterConfig config = RateLimiterConfig.custom().build();
        when(mockRateLimiter.getRateLimiterConfig()).thenReturn(config);

        RequestNotPermitted ex = RequestNotPermitted.createRequestNotPermitted(mockRateLimiter);

        assertThrows(RateLimiterException.class,
                () -> restSearchController.fallbackGetSearchReservation(searchId, ex));
    }
}