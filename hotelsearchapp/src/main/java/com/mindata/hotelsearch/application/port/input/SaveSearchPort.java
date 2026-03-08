package com.mindata.hotelsearch.application.port.input;

import java.time.LocalDate;
import java.util.List;

public interface SaveSearchPort {
	String save(String hotelId,
	        LocalDate checkIn,
	        LocalDate checkOut,
	        List<Integer> ages);
}
