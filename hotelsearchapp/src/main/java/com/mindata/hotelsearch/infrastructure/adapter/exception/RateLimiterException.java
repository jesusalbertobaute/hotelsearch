package com.mindata.hotelsearch.infrastructure.adapter.exception;

public class RateLimiterException extends RuntimeException {
	private static final long serialVersionUID = -5265489258527558500L;

	public RateLimiterException(String message) {
		super(message);
	}


}
