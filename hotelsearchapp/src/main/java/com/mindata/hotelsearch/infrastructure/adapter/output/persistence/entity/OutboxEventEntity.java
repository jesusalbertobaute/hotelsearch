package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDateTime;

import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.converter.BooleanToNumberConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_event")
public class OutboxEventEntity {
	@Id
    @Column(name = "EVENT_ID")
    private String eventId;

    @Column(name = "SEARCH_ID")
    private String searchId;

    @Column(name = "TYPE")
    private String type;

    @Lob
    @Column(name = "PAYLOAD")
    private String payload;

    @Column(name = "PUBLISHED")
    @Convert(converter = BooleanToNumberConverter.class)
    private boolean published = false;

    @Column(name = "PROCESSING")
    @Convert(converter = BooleanToNumberConverter.class)
    private boolean processing = false;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OutboxEventEntity instance;

        private Builder() {
            instance = new OutboxEventEntity();
        }

        public Builder eventId(String eventId) {
            instance.setEventId(eventId);
            return this;
        }

        public Builder searchId(String searchId) {
            instance.setSearchId(searchId);
            return this;
        }

        public Builder type(String type) {
            instance.setType(type);
            return this;
        }

        public Builder payload(String payload) {
            instance.setPayload(payload);
            return this;
        }

        public Builder published(boolean published) {
            instance.setPublished(published);
            return this;
        }

        public Builder processing(boolean processing) {
            instance.setProcessing(processing);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            instance.setCreatedAt(createdAt);
            return this;
        }

        public OutboxEventEntity build() {
            return instance;
        }
    }

}
