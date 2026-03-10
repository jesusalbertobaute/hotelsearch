package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.cleaner;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventProcessedCleanupScheduler {

    private final EventProcessedCleaner cleaner;
    private static final int BATCH_SIZE = 100;

    public EventProcessedCleanupScheduler(EventProcessedCleaner cleaner) {
        this.cleaner = cleaner;
    }

    @Scheduled(fixedRate = 10 * 60 * 60 * 1000,initialDelay = 5 * 60 * 1000) // cada 10 horas con 5 minutos de atraso
    public void runCleanup() {
        LocalDateTime threshold =  LocalDateTime.now().minusDays(1);
        cleaner.cleanOldEvents(threshold, BATCH_SIZE);
    }
}
