package com.mindata.hotelsearch.application.port.output;

import com.mindata.hotelsearch.domain.model.Search;

public interface UpdateSearchCountOutputPort {
	void incrementSearchCount(String requestId,Search search);
}
