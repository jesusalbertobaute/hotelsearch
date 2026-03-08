package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "outbox_events")
@Getter @Setter @Builder
public class OutboxEventEntity {
	@Id
    private String eventId;

    private String searchId;

    private String type;

    @Lob
    private String payload;

    private boolean published = false;

    private boolean processing = false;

    private LocalDateTime createdAt = LocalDateTime.now();

}
