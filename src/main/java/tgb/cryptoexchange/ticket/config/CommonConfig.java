package tgb.cryptoexchange.ticket.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.FixedBackOff;
import tgb.cryptoexchange.ticket.kafka.TicketRequest;
import tgb.cryptoexchange.ticket.kafka.TicketConsumerErrorService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Configuration
@EnableAsync
public class CommonConfig {

    @Bean
    @Profile("!kafka-disabled")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public ConsumerFactory<String, TicketRequest> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, TicketRequest.KafkaDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TicketRequest> kafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            TicketConsumerErrorService ticketConsumerErrorService) {
        ConcurrentKafkaListenerContainerFactory<String, TicketRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        factory.setCommonErrorHandler(defaultErrorHandler(ticketConsumerErrorService));
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(TicketConsumerErrorService ticketConsumerErrorService) {
        return new DefaultErrorHandler(
                ticketConsumerErrorService::handle,
                new FixedBackOff(60000, 1)
        );
    }

    @Bean(name = "detailsRequestSearchExecutor")
    public ThreadPoolTaskExecutor detailsRequestSearchExecutor(
            @Value("${details.executor.core-pool-size}") Integer corePoolSize,
            @Value("${details.executor.max-pool-size}") Integer maxPoolSize,
            @Value("${details.executor.queue-capacity}") Integer queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("DetailsRequestSearch-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Map<Long, Future<Void>> activeSearchMap() {
        return new ConcurrentHashMap<>();
    }

}
