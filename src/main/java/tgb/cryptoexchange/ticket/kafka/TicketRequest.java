package tgb.cryptoexchange.ticket.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;
import tgb.cryptoexchange.exception.DeserializeEventException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketRequest {

    private Long id;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant creationDate;

    private String initiatorApp;

    private Long userId;

    private String category;

    private String description;

    private List<String> fileIds;

    public static class KafkaDeserializer implements Deserializer<TicketRequest> {

        private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        @Override
        public TicketRequest deserialize(String topic, byte[] data) {
            try {
                if (data == null) return null;
                return objectMapper.readValue(data, TicketRequest.class);
            } catch (Exception e) {
                throw new DeserializeEventException("Error occurred while deserializer value: " + new String(data, StandardCharsets.UTF_8), e);
            }
        }
    }

}
