package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.cleaner;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.OutboxEventRepository;

@Component
public class OutboxEventCleaner {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventCleaner.class);

    private final OutboxEventRepository repository;

    public OutboxEventCleaner(OutboxEventRepository repository) {
        this.repository = repository;
    }

    public void cleanOldEvents(LocalDateTime threshold,int batchSize) {
    	 List<String> ids;
         do {
             ids = this.repository.fetchPublishDeleteBatchIds(threshold, batchSize);
             if (!ids.isEmpty()) {
                 int deleted = this.repository.deleteOldPublished(ids);
                 log.info("Deleted {} old processed events in this batch", deleted);
             }
         } while (!ids.isEmpty());
    }
}
