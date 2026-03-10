package com.mindata.hotelsearch.infrastructure.adapter.input.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.mindata.hotelsearch.domain.exception.DateRangeNotAllowedException;
import com.mindata.hotelsearch.domain.exception.DomainException;
import com.mindata.hotelsearch.domain.exception.InvalidSearchIdException;
import com.mindata.hotelsearch.infrastructure.adapter.exception.RateLimiterException;

import jakarta.validation.ConstraintViolationException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleDateRangeNotAllowed() {
        DateRangeNotAllowedException ex = new DateRangeNotAllowedException("Invalid date range");

        ResponseEntity<Map<String, Object>> response = handler.handleDateRangeNotAllowed(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid date range", response.getBody().get("message"));
    }

    @Test
    void handleInvalidSearchId() {
        InvalidSearchIdException ex = new InvalidSearchIdException("Invalid id");

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidSearchId(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid id", response.getBody().get("message"));
    }

    @Test
    void handleDomain() {
        DomainException ex = new DomainException("Domain error");

        ResponseEntity<Map<String, Object>> response = handler.handleDomain(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Domain error", response.getBody().get("message"));
    }

    @Test
    void handleValidationBody() {
    	MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getMessage()).thenReturn("Validation error");

        ResponseEntity<Map<String, Object>> response = handler.handleValidationBody(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody().get("message"));
    }

    @Test
    void handleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("param", "String");

        ResponseEntity<Map<String, Object>> response = handler.handleMissingServletRequestParameter(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody().get("message"));
    }

    @Test
    void handleValidationParam() {
        ConstraintViolationException ex = new ConstraintViolationException("Constraint error", null);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationParam(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Constraint error", response.getBody().get("message"));
    }

    @Test
    void handleRateLimiter() {
        RateLimiterException ex = new RateLimiterException("Too many requests");

        ResponseEntity<Map<String, Object>> response = handler.handleRateLimiter(ex);

        assertEquals(429, response.getStatusCode().value());
        assertEquals("Too many requests", response.getBody().get("message"));
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<Map<String, Object>> response = handler.handleException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected error", response.getBody().get("message"));
    }
}