package tgb.cryptoexchange.ticket.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;
import tgb.cryptoexchange.ticket.exception.DeserializeEventException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketReceive {

    private String appId;

    private Long userId;

    private String category;

    private String description;

    private List<String> fileIds;

    public static class KafkaDeserializer implements Deserializer<TicketReceive> {

        private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        @Override
        public TicketReceive deserialize(String topic, byte[] data) {
            try {
                if (data == null) return null;
                return objectMapper.readValue(data, TicketReceive.class);
            } catch (Exception e) {
                throw new DeserializeEventException("Error occurred while deserializer value: " + new String(data, StandardCharsets.UTF_8), e);
            }
        }
    }

}
