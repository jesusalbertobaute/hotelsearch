package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EVENT_PROCESSED")
public class EventProcessedEntity {
	@Id
	@Column(name = "EVENT_ID")
    private String eventId;
	
	@Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

	public EventProcessedEntity() {

	}

	public EventProcessedEntity(String eventId, LocalDateTime createdAt) {
		super();
		this.eventId = eventId;
		this.createdAt = createdAt;
	}
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
