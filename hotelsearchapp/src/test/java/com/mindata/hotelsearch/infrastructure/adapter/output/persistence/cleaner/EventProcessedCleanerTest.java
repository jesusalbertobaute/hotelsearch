package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.cleaner;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.EventProcessedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventProcessedCleanerTest {

    private EventProcessedRepository repository;
    private EventProcessedCleaner cleaner;

    @BeforeEach
    void setUp() {
        repository = mock(EventProcessedRepository.class);
        cleaner = new EventProcessedCleaner(repository);
    }

    @Test
    void testCleanOldEvents_multipleBatches() {
        LocalDateTime threshold = LocalDateTime.now();
        int batchSize = 2;

        List<String> batch1 = Arrays.asList("id1", "id2");
   
        List<String> batch2 = Arrays.asList("id3");

        List<String> batch3 = Collections.emptyList();

        when(repository.fetchNextBatchIds(threshold, batchSize))
                .thenReturn(batch1)
                .thenReturn(batch2)
                .thenReturn(batch3);

        when(repository.deleteBatchByIds(batch1)).thenReturn(batch1.size());
        when(repository.deleteBatchByIds(batch2)).thenReturn(batch2.size());


        cleaner.cleanOldEvents(threshold, batchSize);

        verify(repository, times(3)).fetchNextBatchIds(threshold, batchSize);
   
        verify(repository, times(1)).deleteBatchByIds(batch1);
        verify(repository, times(1)).deleteBatchByIds(batch2);

        verify(repository, never()).deleteBatchByIds(batch3);
    }

    @Test
    void testCleanOldEvents_noBatches() {
        LocalDateTime threshold = LocalDateTime.now();
        int batchSize = 5;

        when(repository.fetchNextBatchIds(threshold, batchSize))
                .thenReturn(Collections.emptyList());

        cleaner.cleanOldEvents(threshold, batchSize);

        verify(repository, times(1)).fetchNextBatchIds(threshold, batchSize);

        verify(repository, never()).deleteBatchByIds(any());
    }
}