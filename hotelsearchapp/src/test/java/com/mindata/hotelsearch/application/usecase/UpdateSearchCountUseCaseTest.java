package com.mindata.hotelsearch.application.usecase;

import java.time.LocalDate;
import java.util.List;

import com.mindata.hotelsearch.application.port.output.UpdateSearchCountOutputPort;
import com.mindata.hotelsearch.domain.exception.DomainException;
import com.mindata.hotelsearch.domain.model.Search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UpdateSearchCountUseCaseTest {

    private UpdateSearchCountOutputPort outputPort;
    private UpdateSearchCountUseCase useCase;

    @BeforeEach
    void setUp() {
        this.outputPort = mock(UpdateSearchCountOutputPort.class);
        this.useCase = new UpdateSearchCountUseCase(this.outputPort);
    }

    @Test
    void incrementSearchCountCallsOutputPort() {

        String requestId = "req-123";
        String searchId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String hotelId = "hotel1";

        LocalDate checkIn = LocalDate.of(2026, 6, 1);
        LocalDate checkOut = LocalDate.of(2026, 6, 5);

        List<Integer> ages = List.of(30, 25);

        this.useCase.incrementSearchCount(
                requestId,
                searchId,
                hotelId,
                checkIn,
                checkOut,
                ages
        );

        ArgumentCaptor<Search> captor = ArgumentCaptor.forClass(Search.class);

        verify(this.outputPort).incrementSearchCount(
                org.mockito.Mockito.eq(requestId),
                captor.capture()
        );

        Search captured = captor.getValue();

        assertNotNull(captured);
        assertEquals(searchId, captured.getSearchId());
    }

    @Test
    void incrementSearchCountThrowsExceptionWhenRequestIdNull() {

        assertThrows(
                DomainException.class,
                () -> this.useCase.incrementSearchCount(
                        null,
                        "searchId",
                        "hotel",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        List.of(30)
                )
        );
    }

    @Test
    void incrementSearchCountThrowsExceptionWhenRequestIdBlank() {

        assertThrows(
                DomainException.class,
                () -> this.useCase.incrementSearchCount(
                        "   ",
                        "searchId",
                        "hotel",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        List.of(30)
                )
        );
    }

    @Test
    void buildModelCreatesSearch() {

        String searchId = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        String hotelId = "hotelX";

        Search search = this.useCase.buildModel(
                searchId,
                hotelId,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                List.of(20, 30)
        );

        assertNotNull(search);
        assertEquals(searchId, search.getSearchId());
    }
}