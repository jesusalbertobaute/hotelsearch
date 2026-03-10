package com.mindata.hotelsearch.infrastructure.adapter.input.kafka;

import com.mindata.hotelsearch.application.port.input.UpdateSearchCountInputPort;
import com.mindata.hotelsearch.infrastructure.adapter.kafka.event.SearchEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class KafkaConsumerTestConfig {

    @Bean
    @Primary
    public UpdateSearchCountInputPort updateSearchCountInputPort() {
        return mock(UpdateSearchCountInputPort.class);
    }

    @Bean
    public ExecutorService consumerExecutor() {
        return Executors.newSingleThreadExecutor();
    }
    
    @Bean
    public KafkaTemplate<String, SearchEvent> kafkaTemplate(org.springframework.kafka.test.EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }
}