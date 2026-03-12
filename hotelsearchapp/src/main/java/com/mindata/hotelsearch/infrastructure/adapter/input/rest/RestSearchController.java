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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/hotelapi")
@Tag(name = "Hotel Search API", description = "Operations related to hotel search reservations")
public class RestSearchController {
    private final SaveSearchInputPort saveSearchUseCase;
    private final GetSearchInputPort getSearchUseCase;
    
    public RestSearchController(SaveSearchInputPort saveSearchUseCase,
    		GetSearchInputPort getSearchInputUseCase) {
		this.saveSearchUseCase = saveSearchUseCase;
		this.getSearchUseCase = getSearchInputUseCase;
	}
    
    @Operation(
            summary = "Save a hotel search reservation",
            description = "Saves a hotel search and updates the number of times it has been searched."
        )
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Search saved successfully",
                    content = @Content(schema = @Schema(implementation = SearchResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "429", description = "Too many requests"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
    @PostMapping("/search")
    @RateLimiter(name = "saveSearchReservationApiLimiter", fallbackMethod = "fallbackSaveSearchReservation")
    public ResponseEntity<SearchResponseDTO> saveSearchReservation(
    		@io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Search reservation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SearchRequestDTO.class))
            )
    		@Valid @RequestBody SearchRequestDTO searchRequestDTO) {
    	final String searchId= this.saveSearchUseCase.save(searchRequestDTO.hotelId(),searchRequestDTO.checkIn(),
    			searchRequestDTO.checkOut(),searchRequestDTO.ages());
    	final SearchResponseDTO searchResponseDTO = new SearchResponseDTO(searchId);
		return ResponseEntity.status(HttpStatus.CREATED).body(searchResponseDTO);
    }
    
    @Operation(
            summary = "Get search reservation count",
            description = "Returns the number of times a search has been executed using the searchId"
        )
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search found",
                    content = @Content(schema = @Schema(implementation = SearchCountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Search not found"),
            @ApiResponse(responseCode = "400", description = "Invalid searchId"),
            @ApiResponse(responseCode = "429", description = "Too many requests"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
    @GetMapping("/count")
    @RateLimiter(name = "getSearchReservationApiLimiter", fallbackMethod = "fallbackGetSearchReservation")
    public ResponseEntity<SearchCountResponseDTO> getSearchReservation(
            @Parameter(
                    description = "Search identifier returned when creating a search",
                    required = true,
                    example = "_LyMjgjCgc_r3kZwtDQ5XF9vkNOItWehl39EL7yZ4Rk"
            )
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
