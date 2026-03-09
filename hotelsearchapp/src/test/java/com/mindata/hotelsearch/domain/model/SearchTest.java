package com.mindata.hotelsearch.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.mindata.hotelsearch.domain.exception.DateRangeNotAllowedException;
import com.mindata.hotelsearch.domain.exception.DomainException;

class SearchTest {

    @Test
    void testCreateWithSearchDetails() {
        SearchDetails details = new SearchDetails("hotel1",
                LocalDate.of(2026,3,10),
                LocalDate.of(2026,3,15),
                List.of(25,30));

        Search search = Search.create(details);

        assertNotNull(search.getSearchId());
        assertEquals(details, search.getSearchData());
        assertEquals(0, search.getCount());
    }

    @Test
    void testCreateWithSearchIdAndDetails() {
        SearchDetails details = new SearchDetails("hotel2",
                LocalDate.of(2026,4,1),
                LocalDate.of(2026,4,5),
                List.of(20));

        Search search = Search.create("myCustomId", details);

        assertEquals("myCustomId", search.getSearchId());
        assertEquals(details, search.getSearchData());
        assertEquals(0, search.getCount());
    }

    @Test
    void testCreateWithSearchIdDetailsAndCount() {
        SearchDetails details = new SearchDetails("hotel3",
                LocalDate.of(2026,5,1),
                LocalDate.of(2026,5,5),
                List.of(10,15));

        Search search = Search.create("id123", details, 5L);

        assertEquals("id123", search.getSearchId());
        assertEquals(details, search.getSearchData());
        assertEquals(5L, search.getCount());
    }

    @Test
    void testCreateWithSearchDetailsAndCount() {
        SearchDetails details = new SearchDetails("hotel4",
                LocalDate.of(2026,6,1),
                LocalDate.of(2026,6,5),
                List.of(18));

        Search search = Search.create(details, 3L);

        assertNotNull(search.getSearchId());
        assertEquals(details, search.getSearchData());
        assertEquals(3L, search.getCount());
    }

    @Test
    void testNegativeCountThrowsException() {
        SearchDetails details = new SearchDetails("hotel5",
                LocalDate.of(2026,7,1),
                LocalDate.of(2026,7,5),
                List.of(22));

        DomainException exception = assertThrows(DomainException.class, () -> {
            Search.create("id", details, -1);
        });

        assertEquals("count can not be less than 0", exception.getMessage());
    }
 
    @Test
    void testHotelIdEmpty() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new SearchDetails("",
                    LocalDate.of(2026,7,1),
                    LocalDate.of(2026,7,2),
                    List.of(22));
        });

        assertEquals("hotelId cannot be null or blank", exception.getMessage());
    }
    
    @Test
    void testHotelIdNull() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new SearchDetails(null,
                    LocalDate.of(2026,7,1),
                    LocalDate.of(2026,7,10),
                    List.of(22));
        });

        assertEquals("hotelId cannot be null or blank", exception.getMessage());
    }
    
    @Test
    void testCheckInNull() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new SearchDetails("hotel1",
                    null,
                    LocalDate.of(2026,7,10),
                    List.of(22));
        });

        assertEquals("checkIn and checkOut cannot be null", exception.getMessage());
    }

    @Test
    void testCheckOutNull() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new SearchDetails("hotel1",
                    LocalDate.of(2026,7,1),
                    null,
                    List.of(22));
        });

        assertEquals("checkIn and checkOut cannot be null", exception.getMessage());
    }

    @Test
    void testCheckInAfterCheckOut() {
        DateRangeNotAllowedException exception = assertThrows(DateRangeNotAllowedException.class, () -> {
            new SearchDetails("hotel1",
                    LocalDate.of(2026,7,10),
                    LocalDate.of(2026,7,1),
                    List.of(22));
        });

        assertEquals("checkIn cannot be after checkOut", exception.getMessage());
    }

    @Test
    void testAgesNull() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new SearchDetails("hotel1",
                    LocalDate.of(2026,7,1),
                    LocalDate.of(2026,7,10),
                    null);
        });

        assertEquals("ages cannot be empty", exception.getMessage());
    }

    @Test
    void testAgesEmpty() {
        DomainException exception = assertThrows(DomainException.class, () -> {
            new SearchDetails("hotel1",
                    LocalDate.of(2026,7,1),
                    LocalDate.of(2026,7,10),
                    List.of());
        });

        assertEquals("ages cannot be empty", exception.getMessage());
    }

    @Test
    void testGenerateId() {
        LocalDate checkIn = LocalDate.of(2026,8,1);
        LocalDate checkOut = LocalDate.of(2026,8,5);
        String id = Search.generateId("hotelX", checkIn, checkOut, List.of(20,25));

        assertNotNull(id);
        assertFalse(id.isEmpty());
    }

    @Test
    void testGenerateIdThrowsDomainException() throws Exception {
        try (MockedStatic<MessageDigest> mocked = Mockito.mockStatic(MessageDigest.class)) {
            mocked.when(() -> MessageDigest.getInstance("SHA-256"))
                  .thenThrow(new NoSuchAlgorithmException("not found"));

            LocalDate checkIn = LocalDate.of(2026,8,1);
            LocalDate checkOut = LocalDate.of(2026,8,5);

            DomainException exception = assertThrows(DomainException.class,
                () -> Search.generateId("hotelX", checkIn, checkOut, List.of(20)));

            assertEquals("Failed to generate searchId", exception.getMessage());
        }
    }

}
