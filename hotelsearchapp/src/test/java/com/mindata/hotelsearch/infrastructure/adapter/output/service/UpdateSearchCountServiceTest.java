package com.mindata.hotelsearch.infrastructure.adapter.output.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;

class UpdateSearchCountServiceTest {

	private UpdateSearchCountProxy proxy;
    private ExecutorService executor;
    private UpdateSearchCountService service;

    @BeforeEach
    void setUp() {
        this.proxy = mock(UpdateSearchCountProxy.class);

        this.executor = mock(ExecutorService.class);
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executor).submit(any(Runnable.class));

        service = new UpdateSearchCountService(proxy, executor);
    }

    @Test
    void testIncrementSearchCount() {
        String requestId = "AbCdEfGhIjKlMnOpQrStUvWxYz0123456789_ABC";
        SearchDetails details = new SearchDetails("hotel1",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10),
                List.of(22, 30));
        Search search = Search.create(details);

        service.incrementSearchCount(requestId, search);

        ArgumentCaptor<ReservationSearchEntity> captor = ArgumentCaptor.forClass(ReservationSearchEntity.class);
        verify(proxy, times(1)).updateSearch(eq(requestId), captor.capture());

        ReservationSearchEntity entity = captor.getValue();
        assertEquals(search.getSearchId(), entity.getSearchId());
        assertEquals(details.hotelId(), entity.getHotelId());
        assertEquals(details.checkIn(), entity.getCheckIn());
        assertEquals(details.checkOut(), entity.getCheckOut());
        assertEquals(details.ages(), entity.getAges());
        assertEquals(1L, entity.getCount());
    }

    @Test
    void testIncrementSearchCountExceptionHandled() {
        String requestId = "AbCdEfGhIjKlMnOpQrStUvWxYz0123456789_ABC";
        SearchDetails details = new SearchDetails("hotel2",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 5),
                List.of(25));
        Search search = Search.create(details);

        doThrow(new RuntimeException("DB error")).when(proxy).updateSearch(eq(requestId), any());

        assertDoesNotThrow(() -> service.incrementSearchCount(requestId, search));
    }
}
