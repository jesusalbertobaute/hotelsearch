package com.mindata.hotelsearch.application.usecase;

import java.time.LocalDate;
import java.util.List;

import com.mindata.hotelsearch.application.port.output.SaveSearchOutputPort;
import com.mindata.hotelsearch.domain.model.Search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SaveSearchUseCaseTest {

    private SaveSearchOutputPort outputPort;
    private SaveSearchUseCase useCase;

    @BeforeEach
    void setUp() {
        outputPort = mock(SaveSearchOutputPort.class);
        useCase = new SaveSearchUseCase(outputPort);
    }

    @Test
    void saveCreatesSearchAndReturnsSearchId() {

        String hotelId = "hotel123";
        LocalDate checkIn = LocalDate.of(2026, 6, 1);
        LocalDate checkOut = LocalDate.of(2026, 6, 5);
        List<Integer> ages = List.of(30, 28);

        String searchId = useCase.save(hotelId, checkIn, checkOut, ages);

        ArgumentCaptor<Search> captor = ArgumentCaptor.forClass(Search.class);
        verify(outputPort).save(captor.capture());

        Search savedSearch = captor.getValue();

        assertNotNull(savedSearch);
        assertEquals(searchId, savedSearch.getSearchId());
        assertNotNull(searchId);
    }
}