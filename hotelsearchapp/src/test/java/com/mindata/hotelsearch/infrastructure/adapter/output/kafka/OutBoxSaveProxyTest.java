package com.mindata.hotelsearch.infrastructure.adapter.output.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindata.hotelsearch.infrastructure.adapter.exception.PersistenceOutboxException;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEventDetails;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

class OutBoxSaveProxyTest {

    @Mock
    private OutboxEventRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutBoxSaveProxy outBoxSaveProxy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.outBoxSaveProxy.setTopic("test-topic");
    }

    private String createValidSearchId() {
        return "SEARCHID_VALID_1234567890_ABCDEFGH_12345";
    }

    private SearchEvent createValidSearchEvent() {
        String searchId = createValidSearchId();
        SearchEventDetails details = new SearchEventDetails(
                "HOTEL1234567890_ABCDEFGH_12345",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 15),
                List.of(25, 30)
        );
        return new SearchEvent("EVENT1234567890_ABCDEFGHIJKLMN", searchId, details);
    }

    @Test
    void testSaveEvent_success() throws Exception {
        SearchEvent searchEvent = createValidSearchEvent();
        String jsonPayload = String.format("{\"eventId\":\"%s\",\"searchId\":\"%s\"}", 
                                            searchEvent.eventId(), searchEvent.searchId());

        when(this.objectMapper.writeValueAsString(searchEvent)).thenReturn(jsonPayload);

        this.outBoxSaveProxy.saveEvent(searchEvent);

        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(this.outboxRepository).save(captor.capture());

        OutboxEventEntity saved = captor.getValue();
        assertEquals(searchEvent.eventId(), saved.getEventId());
        assertEquals(searchEvent.searchId(), saved.getSearchId());
        assertEquals(jsonPayload, saved.getPayload());
        assertEquals("test-topic", saved.getType());
        assertEquals(false, saved.isPublished());
        assertEquals(false, saved.isProcessing());
    }

    @Test
    void testSaveEvent_objectMapperThrows() throws Exception {
        SearchEvent searchEvent = createValidSearchEvent();
        when(this.objectMapper.writeValueAsString(searchEvent)).thenThrow(new RuntimeException("JSON error"));

        assertThrows(PersistenceOutboxException.class, () -> this.outBoxSaveProxy.saveEvent(searchEvent));
    }

    @Test
    void testFallbackSaveSearch() {
        SearchEvent searchEvent = createValidSearchEvent();
        Throwable ex = new RuntimeException("Simulated exception");

        this.outBoxSaveProxy.fallbackSaveSearch(searchEvent, ex);
    }
}