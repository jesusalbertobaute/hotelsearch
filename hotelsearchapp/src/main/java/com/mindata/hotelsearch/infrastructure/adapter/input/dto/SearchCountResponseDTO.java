package com.mindata.hotelsearch.infrastructure.adapter.input.dto;

public record SearchCountResponseDTO(String searchId, SearchDetailsResponseDTO search, long count) {

}
