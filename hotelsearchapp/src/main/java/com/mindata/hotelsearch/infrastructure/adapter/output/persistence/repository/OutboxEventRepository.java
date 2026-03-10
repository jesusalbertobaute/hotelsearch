package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository;

import java.time.LocalDateTime;
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
		    WHERE published = 0
		      AND processing = 0
		      AND ROWNUM <= :batchSize
		    FOR UPDATE SKIP LOCKED
		""", nativeQuery = true)
	List<String> fetchNextBatchIds(@Param("batchSize") int batchSize);
	
	@Transactional
	@Modifying
	@Query("""
	    UPDATE OutboxEventEntity e
	    SET e.processing = true
	    WHERE e.eventId IN :ids
	""")
	int markBatchAsProcessing(List<String> ids);
	
	@Query(value = """
		    SELECT event_id
		    FROM outbox_event
		    WHERE published = 1
		    AND created_at < :threshold
		    AND ROWNUM <= :batchSize
		    FOR UPDATE SKIP LOCKED
		""", nativeQuery = true)
	List<String> fetchPublishDeleteBatchIds(@Param("threshold") LocalDateTime threshold,@Param("batchSize") int batchSize);
	
	@Transactional
	@Modifying
    @Query(value = """
        DELETE FROM outbox_event
        WHERE event_id IN :ids
        """, nativeQuery = true)
    int deleteOldPublished(@Param("ids") List<String> ids);

    List<OutboxEventEntity> findByEventIdIn(List<String> eventIds);
}
