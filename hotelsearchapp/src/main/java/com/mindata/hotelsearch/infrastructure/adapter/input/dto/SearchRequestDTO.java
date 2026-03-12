package com.mindata.hotelsearch.infrastructure.adapter.input.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SearchRequestDTO(
		@Schema(description = "Hotel identifier", example = "hotel123")
		@NotBlank(message = "hotelId is required") String hotelId,
		@Schema(
		        description = "Check-in date",
		        example = "01/06/2026",
		        type = "string",
		        format = "date"
		)
		@NotNull(message = "checkIn is required")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate checkIn,
		@Schema(
		        description = "Check-out date",
		        example = "02/06/2026",
		        type = "string",
		        format = "date"
		)
		@NotNull(message = "checkOut is required")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
		LocalDate checkOut,
		@Schema(description = "ages", example = "[30, 29, 1, 3]")
        @NotEmpty(message = "ages can not be empty") List<Integer> ages) {

}
