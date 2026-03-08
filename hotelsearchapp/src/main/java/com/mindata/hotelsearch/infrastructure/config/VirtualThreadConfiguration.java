package com.mindata.hotelsearch.infrastructure.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class VirtualThreadConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService outboxExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
