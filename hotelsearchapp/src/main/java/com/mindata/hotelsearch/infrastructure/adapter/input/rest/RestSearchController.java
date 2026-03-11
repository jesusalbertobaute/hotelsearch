package com.mindata.hotelsearch.infrastructure.adapter.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindata.hotelsearch.application.port.input.GetSearchInputPort;
import com.mindata.hotelsearch.application.port.input.SaveSearchInputPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.exception.RateLimiterException;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchCountResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchRequestDTO;
import com.mindata.hotelsearch.infrastructure.adapter.input.dto.SearchResponseDTO;
import com.mindata.hotelsearch.infrastructure.adapter.mapping.SearchOutputMapper;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/hotelapi")
public class RestSearchController {
    private final SaveSearchInputPort saveSearchUseCase;
    private final GetSearchInputPort getSearchUseCase;
    
    public RestSearchController(SaveSearchInputPort saveSearchUseCase,
    		GetSearchInputPort getSearchInputUseCase) {
		this.saveSearchUseCase = saveSearchUseCase;
		this.getSearchUseCase = getSearchInputUseCase;
	}
    
    @PostMapping("/search")
    @RateLimiter(name = "saveSearchReservationApiLimiter", fallbackMethod = "fallbackSaveSearchReservation")
    public ResponseEntity<SearchResponseDTO> saveSearchReservation(@Valid @RequestBody SearchRequestDTO searchRequestDTO) {
    	final String searchId= this.saveSearchUseCase.save(searchRequestDTO.hotelId(),searchRequestDTO.checkIn(),
    			searchRequestDTO.checkOut(),searchRequestDTO.ages());
    	final SearchResponseDTO searchResponseDTO = new SearchResponseDTO(searchId);
		return ResponseEntity.status(HttpStatus.CREATED).body(searchResponseDTO);
    }
    
    @GetMapping("/count")
    @RateLimiter(name = "getSearchReservationApiLimiter", fallbackMethod = "fallbackGetSearchReservation")
    public ResponseEntity<SearchCountResponseDTO> getSearchReservation(
    		@RequestParam  
    		@NotBlank(message = "searchId can not be empty") 
            String searchId) {
    	final Search search = this.getSearchUseCase.findSearch(searchId);
    	final SearchCountResponseDTO searchCountResponseDTO = SearchOutputMapper.toSeachCountResponseDTO(search);
        if (searchCountResponseDTO.count()==0) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(searchCountResponseDTO);
        }
		return ResponseEntity.status(HttpStatus.OK).body(searchCountResponseDTO);
    }
    
    public ResponseEntity<SearchResponseDTO> fallbackSaveSearchReservation(SearchRequestDTO searchRequestDTO, RequestNotPermitted  ex) {
        throw new RateLimiterException("Too Many Request in /search");
    }
    
    public ResponseEntity<String> fallbackGetSearchReservation(String searchId, RequestNotPermitted  ex) {
        throw new RateLimiterException("Too Many Request in /count");
    }

}
