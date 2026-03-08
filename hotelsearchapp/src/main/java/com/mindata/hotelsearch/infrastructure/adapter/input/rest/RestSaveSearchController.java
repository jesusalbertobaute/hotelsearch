package com.mindata.hotelsearch.infrastructure.adapter.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindata.hotelsearch.application.port.input.SaveSearchInputPort;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/hotelapi")
public class RestSaveSearchController {
    private final SaveSearchInputPort saveSearchUseCase;
    
    public RestSaveSearchController(SaveSearchInputPort saveSearchUseCase) {
		this.saveSearchUseCase = saveSearchUseCase;
	}
    
    @PostMapping("/search")
    public ResponseEntity<String> saveUserVote(@Valid @RequestBody SearchRequestDTO searchRequestDTO) {
    	final String searchId= this.saveSearchUseCase.save(searchRequestDTO.hotelId(),searchRequestDTO.checkIn(),
    			searchRequestDTO.checkOut(),searchRequestDTO.ages());
		return ResponseEntity.status(HttpStatus.CREATED).body(searchId);
    }

}
