package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.cleaner;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.EventProcessedRepository;

@Component
public class EventProcessedCleaner {

    private static final Logger log = LoggerFactory.getLogger(EventProcessedCleaner.class);

    private final EventProcessedRepository repository;

    public EventProcessedCleaner(EventProcessedRepository repository) {
        this.repository = repository;
    }

    public void cleanOldEvents(LocalDateTime threshold, int batchSize) {
        List<String> ids;
        do {
            ids = repository.fetchNextBatchIds(threshold, batchSize);
            if (!ids.isEmpty()) {
                int deleted = repository.deleteBatchByIds(ids);
                log.info("Deleted {} old processed events in this batch", deleted);
            }
        } while (!ids.isEmpty());

    }
}
