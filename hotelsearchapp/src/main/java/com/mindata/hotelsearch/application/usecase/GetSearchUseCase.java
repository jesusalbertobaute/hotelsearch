package com.mindata.hotelsearch.application.usecase;

import java.util.Optional;
import java.util.regex.Pattern;

import com.mindata.hotelsearch.application.port.input.GetSearchInputPort;
import com.mindata.hotelsearch.application.port.output.GetSearchOutputPort;
import com.mindata.hotelsearch.domain.exception.InvalidSearchIdException;
import com.mindata.hotelsearch.domain.model.Search;


public class GetSearchUseCase implements GetSearchInputPort {
	private final GetSearchOutputPort getSearchOutput;
	
	public static final Pattern SEARCH_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{40,50}$");

	public GetSearchUseCase(GetSearchOutputPort getSearchOutput) {
		this.getSearchOutput = getSearchOutput;
	}

	public Search findSearch(String searchId) {
		if (!this.isValidSearchId(searchId)) {
			throw new InvalidSearchIdException("searchId is not valid");
		}
		return this.getSearchOutput.findSearch(searchId);
	}
	
	protected boolean isValidSearchId(String searchId) {
	    return Optional.ofNullable(searchId)
	            .map(s -> SEARCH_ID_PATTERN.asMatchPredicate().test(s))
	            .orElse(false);
	}

}
