package com.mindata.hotelsearch.infrastructure.adapter.kafka.event;

import java.time.LocalDate;
import java.util.List;

public record SearchEventDetails(String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages) {

}
