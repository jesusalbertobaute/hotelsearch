package com.mindata.hotelsearch.infrastructure.adapter.kafka.event;

import java.time.LocalDateTime;

public record UpdateCountEventError(String eventId,String searchId,SearchEventDetails searchEventDetails,
		String messageError,LocalDateTime time) {

}
