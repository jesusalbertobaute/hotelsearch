package com.mindata.hotelsearch.application.port.output;

import com.mindata.hotelsearch.domain.model.Search;

public interface SaveSearchPort {
	void save(Search searchModel);
}
