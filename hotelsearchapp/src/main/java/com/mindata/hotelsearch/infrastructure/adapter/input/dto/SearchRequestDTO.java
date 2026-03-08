package com.mindata.hotelsearch.infrastructure.adapter.input.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SearchRequestDTO(
		@NotBlank(message = "hotelId is required") String hotelId,
		@NotNull(message = "checkIn is required")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate checkIn,
		@NotNull(message = "checkOut is required")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate checkOut,
        @NotEmpty(message = "ages can not be empty") List<Integer> ages) {

}
