package com.mindata.hotelsearch.infrastructure.adapter.exception;

public class PersistenceOutboxException extends RuntimeException {

	private static final long serialVersionUID = -547649968825860797L;

	public PersistenceOutboxException(String message) {
		super(message);
	}

}
