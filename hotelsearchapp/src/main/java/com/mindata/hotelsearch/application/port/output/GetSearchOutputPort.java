package com.mindata.hotelsearch.application.port.output;

import com.mindata.hotelsearch.domain.model.Search;

public interface GetSearchOutputPort {
     Search findSearch(String searchId);
}
