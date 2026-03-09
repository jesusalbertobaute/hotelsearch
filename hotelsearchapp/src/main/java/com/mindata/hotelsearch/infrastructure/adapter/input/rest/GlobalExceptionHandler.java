package com.mindata.hotelsearch.infrastructure.adapter.input.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mindata.hotelsearch.domain.exception.DateRangeNotAllowedException;
import com.mindata.hotelsearch.domain.exception.DomainException;
import com.mindata.hotelsearch.domain.exception.InvalidSearchIdException;
import com.mindata.hotelsearch.infrastructure.adapter.exception.RateLimiterException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DateRangeNotAllowedException.class)
	public ResponseEntity<Map<String, Object>> handleDateRangeNotAllowed(DateRangeNotAllowedException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(InvalidSearchIdException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidSearchId(InvalidSearchIdException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationBody(MethodArgumentNotValidException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, Object>> handleValidationParam(ConstraintViolationException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(RateLimiterException.class)
	public ResponseEntity<Map<String, Object>> handleRateLimiter(RateLimiterException ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
		Map<String, Object> errorResponse = this.createErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	private Map<String, Object> createErrorResponse(String message, HttpStatus status) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("timestamp", LocalDateTime.now());
		errorResponse.put("status", status.value());
		errorResponse.put("error", status.getReasonPhrase());
		errorResponse.put("message", message);
		return errorResponse;
	}

}