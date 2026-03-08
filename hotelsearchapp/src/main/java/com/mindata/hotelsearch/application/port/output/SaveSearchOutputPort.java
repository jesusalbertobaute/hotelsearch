package com.mindata.hotelsearch.application.port.output;

import com.mindata.hotelsearch.domain.model.Search;

public interface SaveSearchOutputPort {
	void save(Search searchModel);
}
