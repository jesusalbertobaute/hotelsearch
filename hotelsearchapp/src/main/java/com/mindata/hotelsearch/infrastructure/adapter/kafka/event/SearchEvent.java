package com.mindata.hotelsearch.infrastructure.adapter.kafka.event;

public record SearchEvent(String eventId,String searchId,SearchEventDetails searchEventDetails) {

}
