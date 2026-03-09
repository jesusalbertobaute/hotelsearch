package com.mindata.hotelsearch.application.port.input;

import java.time.LocalDate;
import java.util.List;

public interface UpdateSearchCountInputPort {
	void incrementSearchCount(String requestId,String searchId, String hotelId, 
			   LocalDate checkIn, LocalDate checkOut,
			   List<Integer> ages);
}
