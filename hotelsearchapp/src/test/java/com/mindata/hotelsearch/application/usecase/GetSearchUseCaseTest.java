package com.mindata.hotelsearch.application.usecase;

import com.mindata.hotelsearch.application.port.output.GetSearchOutputPort;
import com.mindata.hotelsearch.domain.exception.InvalidSearchIdException;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.domain.model.SearchDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

class GetSearchUseCaseTest {

    private GetSearchOutputPort outputPort;
    private GetSearchUseCase useCase;

    @BeforeEach
    void setUp() {
        outputPort = mock(GetSearchOutputPort.class);
        useCase = new GetSearchUseCase(outputPort);
    }

    @Test
    void findSearchReturnsSearchWhenSearchIdIsValid() {

        String validSearchId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"; 
        Search expected = Search.create(validSearchId, new SearchDetails("hotel1",LocalDate.of(2020, 2, 3), 
        		LocalDate.of(2020, 3, 3), List.of(30,20,12)));

        when(outputPort.findSearch(validSearchId)).thenReturn(expected);

        Search result = useCase.findSearch(validSearchId);

        assertEquals(expected, result);
    }

    @Test
    void findSearchThrowsExceptionWhenSearchIdInvalid() {

        String invalidSearchId = "invalid";

        assertThrows(
                InvalidSearchIdException.class,
                () -> useCase.findSearch(invalidSearchId)
        );
    }

    @Test
    void findSearchThrowsExceptionWhenSearchIdNull() {

        assertThrows(
                InvalidSearchIdException.class,
                () -> useCase.findSearch(null)
        );
    }

    @Test
    void isValidSearchIdReturnsTrueWhenValid() {

        String validSearchId = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";

        boolean result = useCase.isValidSearchId(validSearchId);

        assertTrue(result);
    }

    @Test
    void isValidSearchIdReturnsFalseWhenInvalid() {

        String invalidSearchId = "123";

        boolean result = useCase.isValidSearchId(invalidSearchId);

        assertFalse(result);
    }
}
