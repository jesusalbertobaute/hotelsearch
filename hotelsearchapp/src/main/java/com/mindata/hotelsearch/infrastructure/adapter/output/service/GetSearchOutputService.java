package com.mindata.hotelsearch.infrastructure.adapter.output.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mindata.hotelsearch.application.port.output.GetSearchOutputPort;
import com.mindata.hotelsearch.domain.model.Search;
import com.mindata.hotelsearch.infrastructure.adapter.mapping.SearchOutputMapper;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.entity.ReservationSearchEntity;
import com.mindata.hotelsearch.infrastructure.adapter.output.persistence.repository.ReservationSearchRepository;

@Service
public class GetSearchOutputService implements GetSearchOutputPort {
	private final ReservationSearchRepository reservationSearchRepository;

	public GetSearchOutputService(ReservationSearchRepository reservationSearchRepository) {
		this.reservationSearchRepository = reservationSearchRepository;
	}
	
	@Override
	public Search findSearch(String searchId) {
		Search search = Search.create(searchId, null, 0);
		Optional<ReservationSearchEntity> optional = this.reservationSearchRepository.findById(searchId);
		
		if (optional.isPresent()) {
			search= SearchOutputMapper.toSearch(optional.get());
		}
		
		return search;
	}

}
