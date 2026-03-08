package com.mindata.hotelsearch.infrastructure.adapter.exception;

public class PersistenceOutboxException extends RuntimeException {
	public PersistenceOutboxException(String message) {
		super(message);
	}

}
