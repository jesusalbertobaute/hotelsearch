package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.OutboxEventEntity;

import jakarta.transaction.Transactional;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {
	
	@Query(value = """
		    SELECT event_id
		    FROM outbox_event
		    WHERE published = 0 AND processing = 0
		    ORDER BY create_at
		    FETCH FIRST :batchSize ROWS ONLY
            FOR UPDATE SKIP LOCKED
		""", nativeQuery = true)
	List<String> fetchNextBatchIds(@Param("batchSize") int batchSize);
	
	@Transactional
	@Modifying
	@Query("""
	    UPDATE OutboxEvent e
	    SET e.processing = true
	    WHERE e.eventId IN :ids
	""")
	int markBatchAsProcessing(List<String> ids);
	
	@Modifying
    @Query(value = """
        DELETE FROM outbox_event
        WHERE published = 1
        AND created_at < SYSTIMESTAMP - INTERVAL '1' DAY
        FETCH FIRST :limit ROWS ONLY
        """, nativeQuery = true)
    int deleteOldPublished(@Param("limit") int limit);

    List<OutboxEventEntity> findByEventIdIn(List<String> eventIds);
}
