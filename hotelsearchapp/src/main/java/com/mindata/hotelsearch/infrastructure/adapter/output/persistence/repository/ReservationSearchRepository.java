package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;

public interface ReservationSearchRepository extends JpaRepository<ReservationSearchEntity, String> {
	
	@Modifying
	@Query("""
	UPDATE ReservationSearchEntity r
	SET r.count = r.count + 1
	WHERE r.searchId = :searchId
	""")
	int incrementCount(@Param("searchId") String searchId);
}
