package com.mindata.hotelsearch.infrastructure.adapter.exception;

public class RateLimiterException extends RuntimeException {

	public RateLimiterException(String message) {
		super(message);
	}


}
