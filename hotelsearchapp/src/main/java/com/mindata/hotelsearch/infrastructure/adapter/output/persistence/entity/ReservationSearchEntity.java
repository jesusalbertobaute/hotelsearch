package com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "RESERVATION_SEARCH")
public class ReservationSearchEntity {
	@Column(name = "SEARCH_ID")
	private String searchId;

	@Column(name = "HOTEL_ID")
	private String hotelId;

	@Column(name = "CHECK_IN")
	private LocalDate checkIn;

	@Column(name = "CHECK_OUT")
	private LocalDate checkOut;

	@Column(name = "AGES")
	private List<Integer> ages;

	@Column(name = "COUNT_SEARCH")
	private long count;

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public LocalDate getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(LocalDate checkIn) {
		this.checkIn = checkIn;
	}

	public LocalDate getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(LocalDate checkOut) {
		this.checkOut = checkOut;
	}

	public List<Integer> getAges() {
		return ages;
	}

	public void setAges(List<Integer> ages) {
		this.ages = ages;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
	public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String searchId;
        private String hotelId;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private List<Integer> ages;
        private long count;

        public Builder searchId(String searchId) {
            this.searchId = searchId;
            return this;
        }

        public Builder hotelId(String hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        public Builder checkIn(LocalDate checkIn) {
            this.checkIn = checkIn;
            return this;
        }

        public Builder checkOut(LocalDate checkOut) {
            this.checkOut = checkOut;
            return this;
        }

        public Builder ages(List<Integer> ages) {
            this.ages = ages;
            return this;
        }

        public Builder count(long count) {
            this.count = count;
            return this;
        }

        public ReservationSearchEntity build() {
            ReservationSearchEntity entity = new ReservationSearchEntity();
            entity.setSearchId(searchId);
            entity.setHotelId(hotelId);
            entity.setCheckIn(checkIn);
            entity.setCheckOut(checkOut);
            entity.setAges(ages);
            entity.setCount(count);
            return entity;
        }
    }
	
	

}
