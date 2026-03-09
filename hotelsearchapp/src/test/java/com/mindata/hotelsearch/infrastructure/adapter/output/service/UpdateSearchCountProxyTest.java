package com.mindata.hotelsearch.infrastructure.adapter.output.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.EventProcessedRepository;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.ReservationSearchRepository;

class UpdateSearchCountProxyPatternTest {

    @Mock
    private EventProcessedRepository eventProcessedRepository;

    @Mock
    private ReservationSearchRepository reservationSearchRepository;

    @InjectMocks
    private UpdateSearchCountProxy updateSearchCountProxy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ReservationSearchEntity createValidEntity() {
        ReservationSearchEntity entity = new ReservationSearchEntity();
        entity.setSearchId("TEST_SEARCH_ID_1234567890_ABCDE_67890_12345");
        return entity;
    }

    @Test
    void testIncrementSucceeds() {
        ReservationSearchEntity entity = createValidEntity();

        when(reservationSearchRepository.incrementCount(entity.getSearchId())).thenReturn(1);

        updateSearchCountProxy.updateSearch("req-1", entity);

        verify(eventProcessedRepository).save(any());
        verify(reservationSearchRepository).incrementCount(entity.getSearchId());
        verify(reservationSearchRepository, never()).save(any());
    }

    @Test
    void testIncrementZero_thenSaveSucceeds() {
        ReservationSearchEntity entity = createValidEntity();

        when(reservationSearchRepository.incrementCount(entity.getSearchId())).thenReturn(0);

        updateSearchCountProxy.updateSearch("req-2", entity);

        verify(reservationSearchRepository).save(entity);
    }

    @Test
    void testSaveThrowsDataIntegrityViolation() {
        ReservationSearchEntity entity = createValidEntity();

        when(reservationSearchRepository.incrementCount(entity.getSearchId())).thenReturn(0);
        doThrow(new DataIntegrityViolationException("Duplicate"))
                .when(reservationSearchRepository).save(entity);

        updateSearchCountProxy.updateSearch("req-3", entity);

        verify(reservationSearchRepository).save(entity);
    }

    @Test
    void testEventAlreadyProcessed() {
        ReservationSearchEntity entity = createValidEntity();

        doThrow(new DataIntegrityViolationException("Duplicate")).when(eventProcessedRepository).save(any());

        updateSearchCountProxy.updateSearch("req-4", entity);

        verify(reservationSearchRepository, never()).incrementCount(any());
        verify(reservationSearchRepository, never()).save(any());
    }

    @Test
    void testFallbackMethodCalled() {
        ReservationSearchEntity entity = createValidEntity();

        Throwable ex = new RuntimeException("Simulated failure");

        updateSearchCountProxy.fallbackUpdateSearchReservation("req-5", entity, ex);

    }
}