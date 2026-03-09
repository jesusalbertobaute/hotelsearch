package com.mindata.hotelsearch.application.port.input;

import com.mindata.hotelsearch.domain.model.Search;

public interface GetSearchInputPort {
	Search findSearch(String searchId);

}
