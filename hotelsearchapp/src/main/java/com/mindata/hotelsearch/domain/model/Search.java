package com.mindata.hotelsearch.domain.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mindata.hotelsearch.domain.exception.DomainException;

import jakarta.xml.bind.DatatypeConverter;

public final class Search {
    private static final Logger log = LoggerFactory.getLogger(Search.class);
    
	private final String searchId;
    private final SearchDetails searchData;
    private final long count;

    private Search(String searchId, SearchDetails searchData) {
        this.searchId = searchId;
        this.searchData = searchData;
        this.count = 0L;
    }
    
    private Search(String searchId, SearchDetails searchData,long count) {
    	if (count < 0L) {
    		throw new DomainException("count can not be less than 0");
    	}
        this.searchId = searchId;
        this.searchData = searchData;
        this.count = count;
    }

    public static Search create(SearchDetails searchData) {
	     String generatedId = generateId(searchData.hotelId(),
	    		  searchData.checkIn(),
	        	  searchData.checkOut(),
	        	  searchData.ages());
	     return new Search(generatedId, searchData);
    }
    
    public static Search create(String searchId,SearchDetails searchData) {
	     return new Search(searchId, searchData);
    }
    
    public static Search create(String searchId,SearchDetails searchData,long count) {
	     return new Search(searchId, searchData,count);
    }
    
    public static Search create(SearchDetails searchData,long count) {
	     String generatedId = generateId(searchData.hotelId(),
	    		  searchData.checkIn(),
	        	  searchData.checkOut(),
	        	  searchData.ages());
	     return new Search(generatedId, searchData,count);
   }
    
    public static String generateId(String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            List<Integer> ages){
    	try {
	    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    	
	    	String agesString = ages.stream()
	    			.map(String::valueOf)
	    			.collect(Collectors.joining(","));
	    	
	    	DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
	    	
	    	String hashString = String.format("%s|%s|%s|%s", hotelId, checkIn.format(formatter), 
	    			checkOut.format(formatter), agesString);
	    	byte[] hashBytes = digest.digest(hashString.getBytes(StandardCharsets.UTF_8));
	    	return Base64.getUrlEncoder()
	                .withoutPadding()
	                .encodeToString(hashBytes);
    	 }catch(NoSuchAlgorithmException exception) {
         	log.error(String.format("Failed to generate searchId: %s", exception.getMessage()));
         	throw new DomainException("Failed to generate searchId");
         }
    }

	public String getSearchId() {
		return searchId;
	}

	public SearchDetails getSearchData() {
		return searchData;
	}

	public long getCount() {
		return count;
	}

}
