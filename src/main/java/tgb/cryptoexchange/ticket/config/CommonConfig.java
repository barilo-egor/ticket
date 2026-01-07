package tgb.cryptoexchange.ticket.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.FixedBackOff;
import tgb.cryptoexchange.ticket.kafka.ConsumerErrorService;
import tgb.cryptoexchange.ticket.kafka.TicketReplyRequest;
import tgb.cryptoexchange.ticket.kafka.TicketRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Configuration
@EnableAsync
public class CommonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public ConsumerFactory<String, TicketRequest> consumerTicketFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, TicketRequest.KafkaDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, TicketReplyRequest> consumerTicketReplyFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, TicketReplyRequest.KafkaDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TicketRequest> ticketListenerFactory(
            KafkaProperties kafkaProperties,
            ConsumerErrorService ticketConsumerErrorService) {
        ConcurrentKafkaListenerContainerFactory<String, TicketRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerTicketFactory(kafkaProperties));
        factory.setCommonErrorHandler(defaultErrorHandler(ticketConsumerErrorService));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TicketReplyRequest> ticketReplyListenerFactory(
            KafkaProperties kafkaProperties,
            ConsumerErrorService ticketConsumerErrorService) {
        ConcurrentKafkaListenerContainerFactory<String, TicketReplyRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerTicketReplyFactory(kafkaProperties));
        factory.setCommonErrorHandler(defaultErrorHandler(ticketConsumerErrorService));
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(ConsumerErrorService ticketConsumerErrorService) {
        return new DefaultErrorHandler(
                ticketConsumerErrorService::handle,
                new FixedBackOff(60000, 1)
        );
    }

    @Bean(name = "ticketRequestSaveExecutor")
    public ThreadPoolTaskExecutor detailsRequestSearchExecutor(
            @Value("${details.executor.core-pool-size}") Integer corePoolSize,
            @Value("${details.executor.max-pool-size}") Integer maxPoolSize,
            @Value("${details.executor.queue-capacity}") Integer queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("TicketRequestSave-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Map<Long, Future<Void>> activeSearchMap() {
        return new ConcurrentHashMap<>();
    }

}
