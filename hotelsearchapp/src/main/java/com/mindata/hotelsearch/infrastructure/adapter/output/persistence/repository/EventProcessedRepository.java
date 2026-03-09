package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.EventProcessedEntity;

public interface EventProcessedRepository extends JpaRepository<EventProcessedEntity, String> {

}
