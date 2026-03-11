package com.mindata.hotelsearch.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mindata.hotelsearch.application.port.input.GetSearchInputPort;
import com.mindata.hotelsearch.application.port.input.SaveSearchInputPort;
import com.mindata.hotelsearch.application.port.input.UpdateSearchCountInputPort;
import com.mindata.hotelsearch.application.port.output.GetSearchOutputPort;
import com.mindata.hotelsearch.application.port.output.SaveSearchOutputPort;
import com.mindata.hotelsearch.application.port.output.UpdateSearchCountOutputPort;
import com.mindata.hotelsearch.application.usecase.GetSearchUseCase;
import com.mindata.hotelsearch.application.usecase.SaveSearchUseCase;
import com.mindata.hotelsearch.application.usecase.UpdateSearchCountUseCase;

@Configuration
public class ApplicationConfiguration {

	@Bean
    public GetSearchInputPort getSearchUseCase(GetSearchOutputPort getSearchOutputPort) {
        return new GetSearchUseCase(getSearchOutputPort);
    }
	
	@Bean
	public SaveSearchInputPort saveSearchUseCase(SaveSearchOutputPort saveSearchOutputPort) {
		return new SaveSearchUseCase(saveSearchOutputPort);
	}
	
	@Bean
	public UpdateSearchCountInputPort updateSearchCountUseCase(UpdateSearchCountOutputPort updateSearchCountOutputPort) {
		return new UpdateSearchCountUseCase(updateSearchCountOutputPort);
	}

}
