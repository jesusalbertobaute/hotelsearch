package com.mindata.hotelsearch.infrastructure.adapter.input.kafka;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import com.mindata.hotelsearch.application.port.input.UpdateSearchCountInputPort;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEventDetails;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = { "hotel_availability_searches" },
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" }
)
@ActiveProfiles("test")
class SearchKafkaConsumerIntegrationTest {

   @Autowired
    private KafkaTemplate<String, SearchEvent> kafkaTemplate;

    @Autowired
    private UpdateSearchCountInputPort updateSearchCountUseCase;
    
    @Autowired
    private KafkaListenerEndpointRegistry registry;
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        UpdateSearchCountInputPort updateSearchCountInputPort() {
            return mock(UpdateSearchCountInputPort.class);
        }
    }
    
    @BeforeEach
    void startListenerContainers() {
     
        this.registry.getListenerContainers().forEach(container -> container.start());
    }

    @Test
    void kafkaListenerConsumesEventAndCallsUseCase() throws InterruptedException {

        SearchEventDetails details = new SearchEventDetails(
                "hotel123",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                List.of(30, 25)
        );
        
        String searchId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        String eventId = "550e8400-e29b-41d4-a716-446655440000";

        SearchEvent event = new SearchEvent(
        		eventId,
                searchId,
                details
        );

        this.kafkaTemplate.send("hotel_availability_searches", event);

        Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(updateSearchCountUseCase)
                .incrementSearchCount(
                        eventId,
                        searchId,
                        details.hotelId(),
                        details.checkIn(),
                        details.checkOut(),
                        details.ages()
                ));
    }
}