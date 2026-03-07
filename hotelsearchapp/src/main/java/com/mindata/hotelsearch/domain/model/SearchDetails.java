package com.mindata.hotelsearch.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.mindata.hotelsearch.domain.exception.DateRangeNotAllowedException;
import com.mindata.hotelsearch.domain.exception.DomainException;

public record SearchDetails(String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages) {
	
	public SearchDetails {

        if (hotelId == null || hotelId.isBlank()) {
            throw new DomainException("hotelId cannot be null or blank");
        }

        if (checkIn == null || checkOut == null) {
            throw new DomainException("checkIn and checkOut cannot be null");
        }

        if (checkIn.isAfter(checkOut)) {
            throw new DateRangeNotAllowedException("checkIn cannot be after checkOut");
        }

        if (ages == null || ages.isEmpty()) {
            throw new DomainException("ages cannot be empty");
        }

        ages = List.copyOf(ages);
    }

}
