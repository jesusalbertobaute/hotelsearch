package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.cleaner;	

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventCleanupScheduler {

    private final OutboxEventCleaner cleaner;
    private static final int BATCH_SIZE = 100;

    public OutboxEventCleanupScheduler(OutboxEventCleaner cleaner) {
        this.cleaner = cleaner;
    }

   
    @Scheduled(fixedRate = 8 * 60 * 60 * 1000,initialDelay = 5 * 60 * 1000) //Cada 8 horas con 5 minutos de atraso
    public void runCleanup() {
    	LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        cleaner.cleanOldEvents(threshold,BATCH_SIZE);
    }
}