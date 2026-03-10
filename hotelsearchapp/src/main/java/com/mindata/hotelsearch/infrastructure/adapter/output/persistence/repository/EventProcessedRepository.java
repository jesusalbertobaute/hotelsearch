package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.EventProcessedEntity;

import jakarta.transaction.Transactional;

public interface EventProcessedRepository extends JpaRepository<EventProcessedEntity, String> {
	
    @Query(value = """
        SELECT event_id
        FROM event_processed
        WHERE created_at < :threshold
        AND ROWNUM <= :batchSize
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<String> fetchNextBatchIds(@Param("threshold") LocalDateTime threshold,
                                   @Param("batchSize") int batchSize);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM event_processed
        WHERE event_id IN :ids
    """, nativeQuery = true)
    int deleteBatchByIds(@Param("ids") List<String> ids);
	
}
